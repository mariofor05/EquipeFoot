package apps.EDF

object PlayersDb:

  // Modèle "catalogue" pour les joueurs prédéfinis
  final case class PlayerTemplate(
    name: String,
    overall: Int,
    defaultNumber: Int,
    preferred: Position,
    others: Seq[Position] = Seq.empty
  )

  // Base de données des joueurs dispo (à compléter avec tes joueurs)
  val all: Vector[PlayerTemplate] = Vector(
    PlayerTemplate("Cristiano Ronaldo", 91, 7, Position.BU, Seq(Position.AD, Position.AG)),
    PlayerTemplate("Ronaldinho", 90, 10, Position.MOC, Seq(Position.AG)),
    PlayerTemplate("Ronaldo Nazario", 92, 9, Position.BU),
    PlayerTemplate("Lionel Messi", 93, 10, Position.AD, Seq(Position.MOC)),
    PlayerTemplate("Kylian Mbappé", 92, 7, Position.BU, Seq(Position.AG)),
    PlayerTemplate("Andrés Iniesta", 89, 8, Position.MC, Seq(Position.MOC)),
    PlayerTemplate("Xavi Hernández", 89, 6, Position.MC)
    // ajoute ici tous les joueurs de ton club (FC26 ou autre)
  )

object PlayerSearch:

  import PlayersDb.*

  private def normalize(s: String): String =
    s.trim.toLowerCase

  /** Cherche les joueurs dont le nom contient la requête, triés par note décroissante. */
  def search(query: String, limit: Int = 10): Vector[PlayerTemplate] =
    val q = normalize(query)
    if q.length < 3 then Vector.empty
    else
      all
        .filter(p => normalize(p.name).contains(q))
        .sortBy(p => -p.overall)
        .take(limit)
