package com.organizeme

import com.organizeme.data.models.user.MongoUserDataSource
import com.organizeme.data.models.user.User
import io.ktor.server.application.*
import com.organizeme.plugins.*
import com.organizeme.security.hashing.SHA256HashingService
import com.organizeme.security.token.JwtTokenService
import com.organizeme.security.token.TokenConfig
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val mongoPw = System.getenv("MONGO_PW")
    val dbName = "ktor-organizeMe"
    val db = KMongo.createClient(
        connectionString = "mongodb+srv://Inigo_Carrate:$mongoPw@cluster0.lbwvemq.mongodb.net/$dbName?retryWrites=true&w=majority"
    ).coroutine
        .getDatabase(dbName)
    val userDataSource = MongoUserDataSource(db)

    /*GlobalScope.launch {
        val user = User(
            username = "test",
            password = "test-password",
            salt = "salt"
        )
        userDataSource.insertUser(user)
    }*/

    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()

    configureSerialization()
    configureMonitoring()
    configureSecurity(tokenConfig)
    configureRouting(userDataSource, hashingService, tokenService, tokenConfig)
}
