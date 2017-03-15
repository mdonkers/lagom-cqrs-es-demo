package nl.codecentric.inspection.impl

import java.sql.{Connection, Timestamp}

import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcReadSide
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcSession.tryWith
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext


class InspectionEventProcessor(readSide: JdbcReadSide)(implicit ec: ExecutionContext)
  extends ReadSideProcessor[InspectionEvent] {

  private def processInspectionAdded(connection: Connection,
                               eventElement: EventStreamElement[InspectionAddedEvent]): Unit = {
    tryWith(connection.prepareStatement(
      "INSERT INTO inspections (ucrn, dt_inspected, employee, remarks) VALUES (?, ?, ?, ?)")) { statement =>
      statement.setString(1, eventElement.event.ucrn)
      statement.setTimestamp(2, eventElement.event.dtInspection.map(dt =>
        new Timestamp(dt.getMillis)).getOrElse(new Timestamp(DateTime.now().getMillis)))
      statement.setString(3, eventElement.event.employee.orNull)
      statement.setString(4, eventElement.event.remarks.orNull)
      statement.execute()
    }
  }

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[InspectionEvent] = {
    val builder = readSide.builder[InspectionEvent]("inspectionsoffset")
    builder.setEventHandler(processInspectionAdded)
    builder.build()
  }

  override def aggregateTags: Set[AggregateEventTag[InspectionEvent]] = Set(InspectionEvent.InspectionEventTag)

}
