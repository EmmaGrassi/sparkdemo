package domain

/**
  * Created by efstathiosstergou on 25/10/16.
  */

object District {

  def fromCSV(data: Array[String]): District = {
    val population =  data(3).toLong
    val unemploymentRate = (data(11).toDouble + data(12).toDouble) / 2
    val entrRatio = data(13).toInt / 1000.0
    val crimeRatio = (data(14).toLong + data(15).toLong) / population

    District(data(0), data(1), data(2), population, data(9).toDouble, data(10).toDouble,
             unemploymentRate, entrRatio, crimeRatio)
  }
}

case class District(id: String, name: String, region: String, population: Long,
                    urbanRatio: Double, averageSalary: Double,
                    unemploymentRate: Double, entrepreneurRatio: Double,
                    crimeRatio: Double)
