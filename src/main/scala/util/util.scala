import java.io.File
import java.time.LocalDate
import java.util.regex.Pattern

import domain.{Sex, Male, Female}

/**
  * Created by efstathiosstergou on 19/10/16.
  */
package object util {
  type Splitter = String => Array[String]

  def createSplitter(sep: String): Splitter = {
    (s: String) => s.split(Pattern.quote(sep), -1)
  }

  // format: YYMMDD
  def parseDate(data: String): LocalDate = {
    LocalDate.of(
      1900 + data.substring(0, 2).toInt,
      data.substring(2, 4).toInt,
      data.substring(4, 6).toInt)
  }

  def parseBirthNumber(data: String): (LocalDate, Sex) = {
    val digits = unquote(data)
    val middleTwo = digits.substring(2, 4).toInt
    val (sex, month) = if (middleTwo >= 50) (Female, middleTwo - 50) else (Male, middleTwo)

    val date = LocalDate.of(
      1900 + digits.substring(0, 2).toInt,
      month,
      digits.substring(4, 6).toInt)

    (date, sex)
  }

  lazy val AppLocation = new File(".").getAbsolutePath

  def unquote(str: String): String = str.substring(1, str.length - 1)

  def getPath(segments: String*): String = {
    val parts = Seq(AppLocation, "src", "main", "resources") ++ segments
    parts.mkString(File.separator)
  }
}
