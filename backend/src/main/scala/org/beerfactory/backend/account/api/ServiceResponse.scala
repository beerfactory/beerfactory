/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.account.api

import java.util.UUID

sealed trait ServiceResponse


sealed trait AccountRegisterResult extends ServiceResponse
case class RegistrationFailure(errors: Seq[String]) extends AccountRegisterResult
case object RegistrationSuccess extends AccountRegisterResult

sealed trait AuthenticateResult extends ServiceResponse
case class AuthenticateFailure(errors: Seq[String]) extends AuthenticateResult
case class AuthenticationSuccess(userId: UUID, tokenId: String) extends AuthenticateResult