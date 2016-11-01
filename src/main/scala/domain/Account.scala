package domain

import java.time.LocalDate

import util._

/**
  * Created by efstathiosstergou on 19/10/16.
  */
object Account {
  def fromCSV(data: Array[String]): Account = Account(data(0), data(1), parseDate(data(3)))
}
case class Account(id: String, districtId: String, creationDate: LocalDate)
