package nl.codecentric.inspection.impl

import java.sql.Connection

import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcReadSide
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcSession.tryWith

import scala.concurrent.ExecutionContext


class InspectionEventProcessor(readSide: JdbcReadSide)(implicit ec: ExecutionContext)
  extends ReadSideProcessor[InspectionEvent] {

  private def processVoteAdded(connection: Connection,
                               eventElement: EventStreamElement[FrameworkVotedAddition]): Unit = {
    tryWith(connection.prepareStatement(
      "INSERT INTO votes (framework, score, comment) VALUES (?, ?, ?)")) { statement =>
      statement.setString(1, eventElement.event.framework)
      statement.setInt(2, eventElement.event.score)
      statement.setString(3, eventElement.event.comment.getOrElse(""))
      statement.execute()
    }
  }

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[InspectionEvent] = {
    val builder = readSide.builder[InspectionEvent]("votesoffset")
    builder.setEventHandler(processVoteAdded)
    builder.build()
  }

  override def aggregateTags: Set[AggregateEventTag[InspectionEvent]] = Set(InspectionEvent.InspectionEventTag)

}
