package domain

import util.unquote
/**
  * Created by efstathiosstergou on 19/10/16.
  */


sealed trait DispositionType
case object Owner extends DispositionType
case object Participant extends DispositionType

object Disposition {
  def fromCSV(data: Array[String]): Disposition = {
    val typ =
      if (unquote(data(3)) == "OWNER") Owner
      else if (unquote(data(3)) == "DISPONENT") Participant
      else throw new IllegalArgumentException("Invalid data")

    Disposition(data(0), data(1), data(2), typ)
  }
}

case class Disposition(id: String, clientId: String, accountId: String, typ: DispositionType) {
  def isOwnership = typ == Owner
  def isParticipant = typ == Participant
}
