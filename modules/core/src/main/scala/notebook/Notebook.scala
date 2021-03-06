package notebook

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import notebook.NBSerializer._

case class Notebook(
                     metadata: Option[Metadata] = None,
                     cells: Option[List[Cell]] = Some(Nil),
                     worksheets: Option[List[Worksheet]] = None,
                     autosaved: Option[List[Worksheet]] = None,
                     nbformat: Option[Int] = None
                   ) {
  def name = metadata.map(_.name).getOrElse("Anonymous")
  def normalizedName: String = {
    val n = name.toLowerCase.replaceAll("[^\\.a-z0-9_-]", "-")
    n.dropWhile(_ == '-').reverse.dropWhile(_ == '-').reverse
  }
  def updateMetadata(newMeta: Option[Metadata]): Notebook = this.copy(metadata = newMeta)
  def rawContent = Some(Notebook.serialize(this))
}

object Notebook {
  def getNewUUID: String = java.util.UUID.randomUUID.toString

  def deserialize(str: String): Option[Notebook] = NBSerializer.fromJson(str)

  def serialize(nb: Notebook): String = NBSerializer.toJson(nb)

  def deserializeFuture(str: String)(implicit ex : ExecutionContext): Future[Notebook] = {
    Future(deserialize(str).get)
  }

  def serializeFuture(nb: Notebook)(implicit ex : ExecutionContext): Future[String] = {
    Future(Notebook.serialize(nb))
  }

  @deprecated(message = "Name is confusing. Use deserialize* instead")
  def read(str: String)(implicit ex : ExecutionContext): Future[Notebook] = {
    deserializeFuture(str)
  }

  @deprecated(message = "Name is confusing. use serialize* instead")
  def write(nb: Notebook)(implicit ex : ExecutionContext): Future[String] = {
    serializeFuture(nb)
  }
}

class NotebookNotFoundException(location:String) extends Exception(s"Notebook not found at $location")
