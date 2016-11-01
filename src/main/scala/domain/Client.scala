package domain

import java.time.LocalDate
import util._

/**
  * Created by efstathiosstergou on 19/10/16.
  */
object Client {
  def fromCSV(data: Array[String]): Client = {
    val (birthDate, sex) = parseBirthNumber(data(1))
    Client(data(0), birthDate, sex, data(2))
  }
}

case class Client(id: String, birthday: LocalDate, sex: Sex, districtId: String) {
  // only two sexes in the dataset
  def isMale = sex == Male
  def isFemale = !isMale
}
