package common

import javax.net.ssl._
import play.core.ApplicationProvider
import play.server.api._
/**
  * Created by momos_000 on 2017/9/27.
  */
class CustomSSLEngineProvider (appProvider: ApplicationProvider) extends SSLEngineProvider{

  override def createSSLEngine(): SSLEngine = {
    // change it to your custom implementation
    SSLContext.getDefault.createSSLEngine
  }

}
