package domain

import java.time.LocalDate

/**
  * Created by efstathiosstergou on 19/10/16.
  */


case class Status(running: Boolean, paid: Boolean)

object Loan {
  val FinishedOK = Status(running=false, paid=true)
  val FinishedRed = Status(running=false, paid=false)
  val RunningOK = Status(running=true, paid=true)
  val RunningRed = Status(running=true, paid=false)

  def fromCSV(data: Array[String]): Loan = {
    val status = util.unquote(data(6)) match {
      case "A" => FinishedOK
      case "B" => FinishedRed
      case "C" => RunningOK
      case "D" => RunningRed
    }
    val date = util.parseDate(data(2))
    Loan(data(0), data(1), date, data(3).toLong, data(4).toLong, data(5).toDouble, status)
  }
}


case class Loan(id: String, accountId: String, date: LocalDate, amount: Long,
                duration: Long, payment: Double, status: Status)
