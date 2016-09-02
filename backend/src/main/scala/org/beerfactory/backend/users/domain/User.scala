/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.users.domain

import java.time.OffsetDateTime
import java.util.UUID

case class User(id: UUID,
                login: String,
                password: String,
                email: String,
                emailVerified: Boolean,
                createdOn: OffsetDateTime,
                lastUpdatedOn: Option[OffsetDateTime],
                disabledOn: Option[OffsetDateTime],
                nickName: Option[String],
                firstName: Option[String],
                lastName: Option[String],
                locales: String
                  )