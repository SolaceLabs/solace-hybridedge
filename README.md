# HybridEdge Starter Project

## Introduction

[HybridEdge](https://solace.com/) is a Solace Corporation software product 
that makes it easy to connect existing systems, and in particular messaging systems, to Solace Messaging.
It requires minimum setup and configuration.

The HybridEdge Starter Project allows developers to write more advanced integration products
when more custom logic is required. It is based on Spring Boot and already contains the dependencies
required to connect to a Solace messaging product.

Internally, it uses [Apache Camel](http://camel.apache.org/) to connect with the integration points.

## Usage

* Clone this repository by running
    <pre><code>git clone https://github.com/SolaceProducts/solace-hybridedge.git</code></pre>

* Edit src/main/resources/application.properties, adding the Solace host URL, message VPN name, username, and password as required. Prepend the host URL with ```smfs://``` and use the corresponding TLS/SSL port if you want to use an encrypted connection.
The file should then look something like this:

    ```# Solace credentials```

    ```solace.jms.host=mr-xxxxxx.messaging.solace.cloud:20000```

    ```solace.jms.msgVpn=myvpn```

    ```solace.jms.clientUsername=my-client-username```

    ```solace.jms.clientPassword=a3i5gm9r1n3s05sc3384k9mlhs```

    ```# Camel/Spring config files should be listed here.```

    ```spring.main.sources=hybrid-edge.xml```

    ```# Required so that Camel will keep running```

    ```camel.springboot.main-run-controller=true```

* A sample configuration file is provided in src/main/resources/hybrid-edge.xml. You can test with that, or copy a sample Camel configuration file from the samples directory into the src/main/resources directory, and edit it as required (see below for more details).
You can either rename it to hybrid-edge.xml, or change the spring.main.sources property in application.properties.

* Edit the build.gradle file, adding any required dependencies to Camel connectors such as ActiveMQ or RabbitMQ.

* Make sure the gradlew script is executable and then build the application by running
    <pre><code>./gradlew assemble</code></pre>

* Start the application by running
    <pre><code>java -jar build/libs/solace-hybridedge.jar</code></pre>

## Solace JMS Endpoint Properties

A Solace Camel JMS endpoint has the following structure:

```solace-jms: topic|queue : topicOrQueueName [? property=value [& property=value] ... ]```

For example:

```solace-jms:topic:/my/topic?deliveryMode=2&amp;timeToLive=60000```

Note that in an XML file, ampersands need to be written as ```&amp;``` and the greater-than symbol needs to be written as ```&gt;```.

Because the Solace JMS Camel Component reuses the standard Camel JMS Component, it
supports the same set of properties.

Please see the [Camel JMS Component documentation](http://camel.apache.org/jms.html) for details.


## Solace JMS Connection Factory Properties

Besides the properties on the JMS endpoint, it is necessary to configure the Solace JMS Connection Factory properties.

This is done by editing the Spring application.properties file. An example was given at the top of this document,
but for reference, here is the list of supported properties.

Required:

<pre>
solace.jms.host
solace.jms.msgVpn
solace.jms.clientUsername 
solace.jms.clientPassword
</pre>

Optional:

<pre>
solace.jms.authenticationScheme
solace.jms.clientDescription
solace.jms.compressionLevel
solace.jms.connectionRetries
solace.jms.connectionRetriesPerHost
solace.jms.connectTimeoutInMillis
solace.jms.deliveryMode
solace.jms.directTransport
solace.jms.dynamicDurables
solace.jms.keepAliveCountMax
solace.jms.keepAliveIntervalInMillis
solace.jms.readTimeoutInMillis
solace.jms.reconnectRetries
solace.jms.reconnectRetryWaitInMillis
solace.jms.respectTtl
solace.jms.sslCipherSuites
solace.jms.sslConnectionDowngradeTo
solace.jms.sslExcludedProtocols
solace.jms.sslKeyStore
solace.jms.sslKeyStoreFormat
solace.jms.sslKeyStorePassword
solace.jms.sslPrivateKeyAlias
solace.jms.sslPrivateKeyPassword
solace.jms.sslProtocol
solace.jms.sslTrustedCommonNameList
solace.jms.sslTrustStore
solace.jms.sslTrustStoreFormat
solace.jms.sslTrustStorePassword
solace.jms.sslValidateCertificate
solace.jms.sslValidateCertificateDate
</pre>


Please see the [Solace JMS API documentation](https://docs.solace.com/API-Developer-Online-Ref-Documentation/jms/com/solacesystems/jms/SolConnectionFactory.html)
and the [JMS Properties Reference](https://docs.solace.com/Solace-JMS-API/JMS-Properties-Reference.htm)
for details on these properties.

N.B. The clientDescription property defaults to showing the component and underlying Camel versions, e.g.

```CamelSolaceJMS version 1.0.0 Camel version: 2.21.0```


## Client Authentication

By default, the authenticationScheme property has the value ```AUTHENTICATION_SCHEME_BASIC```. HybridEdge also supports ```AUTHENTICATION_SCHEME_CLIENT_CERTIFICATE```, but not ```AUTHENTICATION_SCHEME_GSS_KRB``` (Kerberos).

## Connection Factory Session Caching

This component uses Spring's CachingConnectionFactory to cache connections. By default the cache size is 10.
You can increase this by setting the property in the application.properties file:

<pre>
solace.jms.sessionCacheSize
</pre>

## Methods of setting properties

Because the component uses Spring, these Solace JMS Connection Factory properties can be set using Java system properties or environment variables rather than using the application.properties file.

### Java system properties

On the command line you can set properties like this:
```java -Dsolace.jms.host=myhost.com ...```

### Environment Variables

You can set the properties as environment variables in this form:

<pre>
SOLACE_JMS_HOST=myhost.com
SOLACE_JMS_MSG_VPN=vpn1
</pre>

## Properties for specific use cases

### Guaranteed Messaging Acknowledgements

Suppose you are subscribing from a Solace instance and publishing to another JMS broker, and you 
don't want to acknowledge a message from Solace until you are sure that the message was received on the other broker.

In this case, you want to ensure that the ```directTransport``` property is false:

```solace.jms.directTransport=false```

Further, you need to ensure that the ```transacted``` property is set on the Solace endpoint, e.g.

```<c:from uri="solace-jms:queue:testQueue?transacted=true"/>```

## Encrypting passwords

HybridEdge supports the [Camel method of encrypting passwords.](http://camel.apache.org/jasypt.html) which uses the jasypt library.

Here are instructions on how to do this:

1. Download these jars from [Maven Central](https://search.maven.org/)

    [camel-jasypt-2.21.1.jar (or later)](https://search.maven.org/#search%7Cga%7C1%7Cg%3Aorg.apache.camel%20AND%20a%3Acamel-jasypt)

    [jasypt-1.9.2.jar (or later)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.jasypt%22%20AND%20a%3A%22jasypt%22)

2. Pick a password that will be used to encrypt and decrypt your Solace password, e.g. 'myCamelPassword'

3. Run this command to encrypt your Solace password, for example 'mySolacePassword':

    ```java -cp "camel-jasypt-2.21.1.jar:jasypt-1.9.2.jar" org.apache.camel.component.jasypt.Main -c encrypt -p myCamelPassword -i mySolacePassword```

    (Note that on Windows, the classpath separator used in the -cp argument should be a semicolon, not a colon). 
    
    Jasypt will respond with something like:

    ```Encrypted text: mCdmWUhQSQu+1AYUGq48R75WfUanyOf3lV6i89IKZt0=```

    Surround the encrypted password with ENC(...) and put it into your application.properties file like this:

    ```solace.jms.clientPassword=ENC(mCdmWUhQSQu+1AYUGq48R75WfUanyOf3lV6i89IKZt0=)```

4. Finally, provide the jasypt password as a system property in the HybridEdge command line or as an environment variable:

    ```java -Djasypt.password=myCamelPassword ...```

    or

    ```export JASYPT_PASSWORD=myCamelPassword```

## Solace JMS Headers

The Solace JMS API generates extra headers:

<pre>
JMS_Solace_DeadMsgQueueEligible
JMS_Solace_DeliverToOne
JMS_Solace_ElidingEligible
</pre>

Although this is permissible according to the JMS specification, some JMS providers might reject messages with these headers. Therefore a processor is included which, when used, will remove any headers with names starting with JMS_Solace.

To use it, add this line as a bean definition to your Camel XML configuration file:

```<bean id="stripSolaceHeadersProcessor" class="com.solace.camel.component.jms.StripSolaceHeadersProcessor"/>```

and then include it in your route like this:

<pre>
&lt;c:from uri="solace-jms:queue:myQueue"/&gt;
&lt;c:bean ref="stripSolaceHeadersProcessor"/&gt;
&lt;c:to uri="wmq:queue:inQueue"/&gt;
</pre>
## Third-Party Components

Here are some product-specific instructions.

### RabbitMQ 

Use the file samples/rabbitmq.xml as a reference. Change the hostname in the endpoint as appropriate. Add any additional properties as required, including credentials.

Configuration instructions are here: http://camel.apache.org/rabbitmq.html


### ActiveMQ

Use the file samples/activemq.xml as a reference. In particular, note the bean configuration:

<pre>
&lt;bean id="activemq" class="org.apache.activemq.camel.component.ActiveMQComponent"&gt;
    &lt;property name="brokerURL" value="tcp://localhost:61616"/&gt;
&lt;/bean&gt;
</pre>

Change the brokerURL property value in the bean definition as appropriate. Add any additional properties as required, including credentials.

Configuration instructions are here: http://camel.apache.org/activemq.html


