package domain

import java.time.LocalDate

/**
  * Created by efstathiosstergou on 25/10/16.
  */

sealed trait TransactionType

case object Credit extends TransactionType

case object Debit extends TransactionType

object Transaction {
  def fromCSV(data: Array[String]): Transaction = {
    val date = util.parseDate(data(2))
    val transType = if (util.unquote(data(3)) == "PRIJEM") Credit else Debit
    Transaction(data(0), data(1), date, transType, data(5).toDouble)

  }
}

case class Transaction(id: String, accountId: String, date: LocalDate, typ: TransactionType,
                       amount: Double) {
  def toCSV: String = productIterator.map(_.toString).mkString(";")
}

