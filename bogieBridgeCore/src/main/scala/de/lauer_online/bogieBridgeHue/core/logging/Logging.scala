package de.lauer_online.bogieBridgeHue.core.logging

/**
 * Custom logger with lazy evaluation
 */

import org.slf4j.{Logger => SLF4JLogger, LoggerFactory}
trait Logger {
  protected val slf4jLogger: SLF4JLogger

  def trace(message: => String) = if(slf4jLogger.isTraceEnabled) slf4jLogger.trace(message)
  def debug(message: => String) = if(slf4jLogger.isDebugEnabled) slf4jLogger.debug(message)
  def info(message: => String) = if(slf4jLogger.isInfoEnabled) slf4jLogger.info(message)
  def warn(message: => String) = if(slf4jLogger.isWarnEnabled) slf4jLogger.warn(message)
  def error(message: => String) = if(slf4jLogger.isErrorEnabled) slf4jLogger.error(message)
}

trait Logging {
  self =>
  protected lazy val logger = new Logger {
    override protected val slf4jLogger = LoggerFactory getLogger self.getClass.getSimpleName
  }
}
