package domain

import java.time.LocalDate

/**
  * Created by efstathiosstergou on 20/10/16.
  */

sealed trait CardType
case object Junior extends CardType
case object Classic extends CardType
case object Gold extends CardType


object Card {
  def fromCSV(data: Array[String]): Card = {
    val date = util.parseDate(data(3))
    val typ = data(2) match {
      case "gold" => Gold
      case "junior" => Junior
      case _ => Classic
    }
    Card(data(0), data(1), typ, date)
  }
}

case class Card(id: String, accountId: String, typ: CardType, issued: LocalDate)
