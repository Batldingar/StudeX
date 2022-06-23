package at.hagenberg.studex.proxy

object ServiceProxyFactory {
    fun createProxy(): ServiceProxy {
        return ServiceProxyImpl()
    }
}