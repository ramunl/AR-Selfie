package us.cyberstar.data.internal.network.s3

import android.content.Context
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import javax.inject.Inject


class S3CredentialsProviderImpl @Inject constructor(private val context: Context, private val token: String) {
    val credentials: CognitoCachingCredentialsProvider by lazy {
        val cognito = CognitoCachingCredentialsProvider(
            context, // Application Context
            token, // Identity Pool ID
            Regions.DEFAULT_REGION
        )
        cognito.token
        cognito
    }

}