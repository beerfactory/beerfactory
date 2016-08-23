/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.account.domain

sealed trait AccountStatus
case object NewAccount extends AccountStatus
case object ConfirmWait extends AccountStatus
case object Confirmed extends AccountStatus
case object Active extends AccountStatus
case object Disabled extends AccountStatus