package org.beerfactory.client.utils

/**
  * Created by njouanin on 18/11/16.
  */
object Forms {
  case class FormError(headerMessage: String, errorFields: Set[String], errorMessages: Seq[String])
}
