package de.lauer_online.bogieBridgeHue.connector.hueBridge

/**
 * Return the color value according to the temperature.
 */
object TempToColor {

  val offset = 41

  val colors= {
    Range(1000, 50000, 750).toList
  }

  def getColorValue(curTemp: Int) = colors(offset - curTemp)

}
