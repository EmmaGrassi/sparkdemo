import domain._
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


  /* transform into pairs */
  val clientPair = clients.map(c => c.id -> c)
  val transactionPair = transactions.map(t => t.accountId -> t)
  val dispositionPair = disposition.map(d => d.accountId -> d.clientId)
  val accountPair = accounts.map(a => a.id -> a)


  /*
    1. Find the top 10 clients. (With more money in their accounts)
      Explain aggregateByKey, why is it better than groupBy

       def aggregateByKey
        (zeroValue: U)
        (seqOp: (U, V) => U,
          combOp: (U, U) => U): RDD[(K, U)]


      Explain join

      transactions -> accountId
      disposition -> clientId, accountId...

  */



  /* be careful of the closure */

  def addAmount(acc: Double, t: Transaction): Double = t.typ match {
    case Debit => acc - t.amount
    case _ => acc + t.amount
  }

    /* RDD[(AccountId, Amount)] */
  val accountBalance = transactionPair.aggregateByKey(0D)(addAmount, _ + _)

  // top10?

  val clientAccountsBalance = dispositionPair.join(accountBalance).map { case (accountId, (clientId, balance)) =>
    clientId -> balance
  }

  val clientsBalance = clientAccountsBalance.aggregateByKey(0.0)( _ + _, _ + _)

  val top10 = clientsBalance.join(clientPair).takeOrdered(10)(new Ordering[(String, (Double, Client))] {
    override def compare(x: (String, (Double, Client)), y: (String, (Double, Client))): Int =
      (y._2._1 - x._2._1).toInt
  })

  top10 foreach println




}
