package nl.codecentric.inspection.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import nl.codecentric.inspection.api.InspectionService
import play.api.db.HikariCPComponents
import play.api.libs.ws.ahc.AhcWSComponents

class InspectionLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new InspectionApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new InspectionApplication(context) with LagomDevModeComponents

  override def describeServices = List(
    readDescriptor[InspectionService]
  )
}

abstract class InspectionApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with JdbcPersistenceComponents
    with HikariCPComponents
    with AhcWSComponents {

  // Bind the services that this server provides
  override lazy val lagomServer = LagomServer.forServices(
    bindService[InspectionService].to(wire[InspectionServiceImpl])
  )

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = InspectionSerializerRegistry

  // Register the Inspection persistent entity
  persistentEntityRegistry.register(wire[InspectionEntity])
}
