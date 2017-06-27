import io.sphere.sdk.client.BlockingSphereClient
import io.sphere.sdk.client.SphereClientConfig
import io.sphere.sdk.client.SphereClientFactory
import io.sphere.sdk.products.search.ProductProjectionSearch
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.Locale.ENGLISH

fun main(args: Array<String>) {
    Ct.createClient().use { client ->//use is like a try-resource block from Java 7, it will close the client finally
        val search = ProductProjectionSearch.ofCurrent().withSort {it.createdAt().desc()}
        val products = client.executeBlocking(search)
        products.results.forEach { product -> println(product.name[ENGLISH]) }
    }
}

object Ct {
    fun createClient(): BlockingSphereClient {
        val config = SphereClientConfig.ofProperties(loadProperties(), "commercetools.")
        val asyncClient = SphereClientFactory.of().createClient(config)
        return BlockingSphereClient.of(asyncClient, 10, TimeUnit.SECONDS)
    }

    //these properties are just for a product read only project, use your credentials
    //for full project access in commercetools.properties
    private fun loadProperties(): Properties =
            javaClass.getResourceAsStream("commercetools.properties").use { propertiesStream ->
        val properties = Properties()
        properties.load(propertiesStream)
        return properties
    }
}
