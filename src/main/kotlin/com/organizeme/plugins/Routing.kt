package com.organizeme.plugins

import com.organizeme.authenticate
import com.organizeme.data.models.user.UserDataSource
import com.organizeme.getSecretInfo
import com.organizeme.security.hashing.HashingService
import com.organizeme.security.token.TokenConfig
import com.organizeme.security.token.TokenService
import com.organizeme.signIn
import com.organizeme.signUp
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        signIn(userDataSource, hashingService, tokenService, tokenConfig)
        signUp(hashingService, userDataSource)
        authenticate()
        getSecretInfo()
    }
}
