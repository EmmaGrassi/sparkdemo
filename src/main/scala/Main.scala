import java.time.LocalDate

import domain.Card
import domain.Debit
import domain.Disposition
import domain.District
import domain.Loan
import domain.Transaction
import domain.{Account, Client}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import util._

import scala.reflect.ClassTag
import scala.util.Try

/**
  * Created by efstathiosstergou on 18/10/16.
  */

object Main extends App {

  val conf = new SparkConf().setAppName("Simple App").setMaster("local[*]")
  val sc = new SparkContext(conf)
  val splitter = createSplitter(";")

  def parseFile[A: ClassTag](rdd: RDD[String], constructor: (Array[String] => A)): RDD[A] =
    rdd.flatMap(line => Try(constructor(splitter(line))).toOption)


  val clientFile = sc.textFile(getPath("client.asc"))
  val accountFile = sc.textFile(getPath("account.asc"))
  val loanFile = sc.textFile(getPath("loan.asc"))
  val cardFile = sc.textFile(getPath("card.asc"))
  val districtFile = sc.textFile(getPath("district.asc"))
  val transFile = sc.textFile(getPath("trans.asc"))
  val dispFile = sc.textFile(getPath("disp.asc"))

  val clients = parseFile(clientFile, Client.fromCSV)
  val accounts = parseFile(accountFile, Account.fromCSV)
  val loans = parseFile(loanFile, Loan.fromCSV)
  val cards = parseFile(cardFile, Card.fromCSV)
  val districts = parseFile(districtFile, District.fromCSV)
  val transactions = parseFile(transFile, Transaction.fromCSV)
  val disposition = parseFile(dispFile, Disposition.fromCSV)


  disposition take 10 foreach println


  /* transform into pairs */
  val clientPair = clients.map(c => c.id -> c)
  val transactionPair = transactions.map(t => t.accountId -> t)
  val dispositionPair = disposition.map(d => d.accountId -> d.clientId)
  val accountPair = accounts.map(a => a.id -> a)


  /* be careful of the closure */
  def addAmount(acc: Double, t: Transaction): Double = t.typ match {
    case Debit => acc - t.amount
    case _ => acc + t.amount
  }

    /* RDD[(AccountId, Amount)] */
  val accountBalance = transactionPair.aggregateByKey(0D)(addAmount, _ + _)


  /* be careful of the closure */
  type UpdateAcc = (Double, LocalDate)
  def updateAmount(acc: UpdateAcc, t: Transaction): UpdateAcc = {
    val (currentValue, currentDate) = acc
    if (t.date.isAfter(currentDate)) (t.balance, t.date)
    else acc
  }

  def updateAccumulators(acc1: UpdateAcc, acc2: UpdateAcc): UpdateAcc =
    if (acc1._2.isAfter(acc2._2)) acc1 else acc2



  def closeEnough(d1: Double, d2: Double, epsilon: Double): Boolean =
    Math.abs(d1 - d2) <= epsilon

  val accountBalance2 = transactionPair.aggregateByKey((0D, LocalDate.MIN))(updateAmount, updateAccumulators)
  val checkPair = accountBalance.join(accountBalance2.map { case (id, (acc, _)) => id -> acc })

  val maxDiff = checkPair.aggregate((0D, "ANY")) ( { case (old@(acc, oldId), (id, (d1, d2))) =>
    val diff = Math.abs(d1 - d2)
      if (diff > acc) diff -> id else old
  }, (t1, t2) => if (t1._1 > t2._1) t1 else t2)

  println(maxDiff)

  /* clients.take(10) foreach println
  accounts.take(10) foreach println
  loans take 10 foreach println
  cards take 10 foreach println
  districts take 10 foreach println
  transactions take 10 foreach println
  */

}
