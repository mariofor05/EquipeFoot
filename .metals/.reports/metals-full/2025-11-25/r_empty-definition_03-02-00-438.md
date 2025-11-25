error id: file:///C:/Users/mario/EquipeFoot/apps/js/src/main/scala/apps/EDF/UI.scala:nonEmpty.
file:///C:/Users/mario/EquipeFoot/apps/js/src/main/scala/apps/EDF/UI.scala
empty definition using pc, found symbol in pc: nonEmpty.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -availableFormations/nonEmpty.
	 -availableFormations/nonEmpty#
	 -availableFormations/nonEmpty().
	 -scala/Predef.availableFormations.nonEmpty.
	 -scala/Predef.availableFormations.nonEmpty#
	 -scala/Predef.availableFormations.nonEmpty().
offset: 16411
uri: file:///C:/Users/mario/EquipeFoot/apps/js/src/main/scala/apps/EDF/UI.scala
text:
```scala
package apps.EDF

import org.scalajs.dom
import org.scalajs.dom.{document, HTMLElement, HTMLInputElement, HTMLSelectElement}
import scala.scalajs.js.annotation.JSExportTopLevel

object UI:

  // État de l’app côté client
  private var state: AppState =
    Logic.init match
      case ServerToClient.StateUpdated(s) => s
      case ServerToClient.Error(msg) =>
        dom.console.error(s"Erreur init: $msg")
        AppState(Nil, None, None)

  // compteur pour fabriquer des ids de joueurs
  private var playerCounter = 0
  private def newPlayerId(): String =
    playerCounter += 1
    s"p$playerCounter"

  @JSExportTopLevel("main")
  def main(): Unit =
    ensureStyles()
    render()

  // injecte une feuille de style
  private def ensureStyles(): Unit =
    val existing = document.getElementById("mfprime-styles")
    if existing == null then
      val style = document.createElement("style")
      style.id = "mfprime-styles"
      style.textContent =
        """
body {
  margin: 0;
  font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
  background: linear-gradient(135deg, #0b1020, #122b4a);
  color: #f5f7ff;
}
#app {
  min-height: 100vh;
}

.mf-app {
  min-height: 100vh;
  padding: 32px 48px;
  box-sizing: border-box;
  background:
    radial-gradient(circle at top left, rgba(80,150,255,0.22), transparent 55%),
    radial-gradient(circle at bottom right, rgba(30,90,200,0.25), transparent 55%);
}

.mf-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.mf-title {
  font-size: 32px;
  font-weight: 700;
}

.mf-brand {
  font-size: 20px;
  font-weight: 600;
  letter-spacing: .08em;
  text-transform: uppercase;
  color: #7fb6ff;
}

.mf-content {
  display: grid;
  grid-template-columns: minmax(0, 420px) minmax(0, 560px);
  gap: 32px;
}

.mf-card {
  background: rgba(4, 12, 32, 0.9);
  border-radius: 16px;
  padding: 20px 24px;
  box-shadow: 0 18px 35px rgba(0,0,0,0.45);
  border: 1px solid rgba(120, 180, 255, 0.25);
}

.mf-card h2,
.mf-card h3 {
  margin: 0 0 16px;
}

.mf-input-row {
  display: grid;
  grid-template-columns: minmax(0, 240px) 84px 96px;
  gap: 8px;
  align-items: center;
}

.mf-input,
.mf-select {
  width: 100%;
  padding: 8px 10px;
  border-radius: 8px;
  border: 1px solid rgba(140, 180, 240, 0.6);
  background: rgba(10, 18, 40, 0.95);
  color: #f5f7ff;
}

.mf-input::placeholder {
  color: rgba(200, 214, 255, 0.6);
}

.mf-button {
  padding: 8px 12px;
  border-radius: 8px;
  border: none;
  background: linear-gradient(135deg, #2f7bff, #5ba9ff);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  transition: transform .08s ease, box-shadow .08s ease, filter .08s ease;
}

.mf-button-small {
  padding: 4px 8px;
  font-size: 12px;
  border-radius: 999px;
}

.mf-button:hover {
  filter: brightness(1.08);
  box-shadow: 0 8px 18px rgba(47, 123, 255, 0.4);
  transform: translateY(-1px);
}

.mf-button:active {
  transform: translateY(0);
  box-shadow: none;
}

.mf-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.mf-team-item {
  padding: 8px 10px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.mf-team-item:hover {
  background: rgba(69, 130, 255, 0.18);
}

.mf-team-item.selected {
  background: rgba(69, 130, 255, 0.32);
}

.mf-muted {
  color: rgba(220, 230, 255, 0.7);
  font-size: 14px;
}

.mf-lineup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.mf-lineup-grid {
  display: grid;
  grid-template-columns: repeat(1, minmax(0, 1fr));
  gap: 4px;
  margin-top: 8px;
}

.mf-lineup-slot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  padding: 4px 6px;
  border-radius: 8px;
  background: rgba(15, 26, 60, 0.9);
}

.mf-lineup-slot-label {
  opacity: 0.9;
}

.mf-lineup-slot-player {
  opacity: 0.9;
}

.mf-pitch {
  margin-top: 12px;
  border-radius: 20px;
  background: linear-gradient(#138f3f, #0c6f30);
  padding: 16px 10px;
  box-shadow: inset 0 0 0 2px rgba(255, 255, 255, 0.4),
              0 14px 30px rgba(0, 0, 0, 0.55);
}

.mf-pitch-row {
  display: flex;
  justify-content: center;
  gap: 18px;
  margin: 10px 0;
}

.mf-pitch-slot {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(10, 40, 120, 0.95);
  border: 2px solid #ffffff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  text-align: center;
  color: #f5f7ff;
  position: relative;
}

.mf-pitch-slot-label {
  font-weight: 600;
}

.mf-pitch-slot-player {
  font-size: 10px;
  opacity: 0.9;
}

.mf-pitch-slot-btn {
  position: absolute;
  bottom: -10px;
  right: -10px;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  cursor: pointer;
  background: #3f8dff;
  color: #fff;
  box-shadow: 0 4px 10px rgba(0,0,0,0.4);
}

.mf-pitch-slot-btn.remove {
  background: #ff5a5a;
}

.mf-pitch {
  margin-top: 12px;
  border-radius: 20px;
  background: linear-gradient(#138f3f, #0c6f30);
  padding: 16px 10px;
  box-shadow: inset 0 0 0 2px rgba(255, 255, 255, 0.4),
              0 14px 30px rgba(0, 0, 0, 0.55);
}

.mf-pitch-row {
  display: flex;
  justify-content: center;
  gap: 18px;
  margin: 10px 0;
}

.mf-pitch-slot {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(10, 40, 120, 0.95);
  border: 2px solid #ffffff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  text-align: center;
  color: #f5f7ff;
  position: relative;
}

.mf-pitch-slot-label {
  font-weight: 600;
}

.mf-pitch-slot-player {
  font-size: 10px;
  opacity: 0.9;
}

.mf-pitch-slot-btn {
  position: absolute;
  bottom: -10px;
  right: -10px;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  cursor: pointer;
  background: #3f8dff;
  color: #fff;
  box-shadow: 0 4px 10px rgba(0,0,0,0.4);
}

.mf-pitch-slot-btn.remove {
  background: #ff5a5a;
}

""".stripMargin
      document.head.appendChild(style)

  // rerender général
  private def render(): Unit =
    val root = document.getElementById("app").asInstanceOf[HTMLElement]
    if root == null then
      dom.console.error("Element #app introuvable dans le HTML")
    else
      root.innerHTML = ""
      root.appendChild(renderApp())

  private def renderApp(): HTMLElement =
    val container = document.createElement("div").asInstanceOf[HTMLElement]
    container.className = "mf-app"

    val header = document.createElement("div").asInstanceOf[HTMLElement]
    header.className = "mf-header"

    val title = document.createElement("div").asInstanceOf[HTMLElement]
    title.className = "mf-title"
    title.textContent = "Créer une équipe"

    val brand = document.createElement("div").asInstanceOf[HTMLElement]
    brand.className = "mf-brand"
    brand.textContent = "mfprime"

    header.appendChild(title)
    header.appendChild(brand)

    val content = document.createElement("div").asInstanceOf[HTMLElement]
    content.className = "mf-content"

    content.appendChild(renderCreateTeamCard())
    content.appendChild(renderDashboardCard())

    container.appendChild(header)
    container.appendChild(content)
    container

  // carte création d’équipe
  private def renderCreateTeamCard(): HTMLElement =
    val card = document.createElement("div").asInstanceOf[HTMLElement]
    card.className = "mf-card"

    val h2 = document.createElement("h2").asInstanceOf[HTMLElement]
    h2.textContent = "Créer une équipe"
    card.appendChild(h2)

    val row = document.createElement("div").asInstanceOf[HTMLElement]
    row.className = "mf-input-row"

    val nameInput = document.createElement("input").asInstanceOf[HTMLInputElement]
    nameInput.`type` = "text"
    nameInput.placeholder = "Nom de l'équipe"
    nameInput.className = "mf-input"
    nameInput.value = "FC Crans"

    val nbInput = document.createElement("input").asInstanceOf[HTMLInputElement]
    nbInput.`type` = "number"
    nbInput.placeholder = "Effectif (ex: 11)"
    nbInput.className = "mf-input"
    nbInput.value = "11"

    val button = document.createElement("button").asInstanceOf[HTMLElement]
    button.className = "mf-button"
    button.textContent = "Créer"

    button.addEventListener(
      "click",
      (_: dom.Event) =>
        val name = nameInput.value.trim
        val nb =
          try nbInput.value.trim.toInt
          catch case _: NumberFormatException => 0

        if name.isEmpty || nb <= 0 then
          dom.window.alert("Entre un nom d'équipe et un nombre de joueurs valide.")
        else
          send(ClientToServer.CreateTeam(name, nb))
    )

    row.appendChild(nameInput)
    row.appendChild(nbInput)
    row.appendChild(button)

    card.appendChild(row)
    card

  // carte de gauche : équipes + compo
  private def renderDashboardCard(): HTMLElement =
    val card = document.createElement("div").asInstanceOf[HTMLElement]
    card.className = "mf-card"

    val h2 = document.createElement("h2").asInstanceOf[HTMLElement]
    h2.textContent = "Tableau de bord"
    card.appendChild(h2)

    card.appendChild(renderTeamsSection())
    card.appendChild(renderLineupSection())

    card

  private def renderTeamsSection(): HTMLElement =
    val section = document.createElement("div").asInstanceOf[HTMLElement]

    val h3 = document.createElement("h3").asInstanceOf[HTMLElement]
    h3.textContent = "Équipes"
    section.appendChild(h3)

    if state.teams.isEmpty then
      val p = document.createElement("p").asInstanceOf[HTMLElement]
      p.className = "mf-muted"
      p.textContent = "Aucune équipe pour l’instant."
      section.appendChild(p)
    else
      val ul = document.createElement("ul").asInstanceOf[HTMLElement]
      ul.className = "mf-list"

      for team <- state.teams do
        val li = document.createElement("li").asInstanceOf[HTMLElement]
        li.className =
          "mf-team-item" + (if state.selectedTeam.contains(team.id) then " selected" else "")

        li.textContent = team.name + s" (${team.players.size}/${team.nbPlayers})"
        li.addEventListener(
          "click",
          (_: dom.Event) => send(ClientToServer.SelectTeam(team.id))
        )

        ul.appendChild(li)

      section.appendChild(ul)

    section

  private def renderLineupSection(): HTMLElement =
    val section = document.createElement("div").asInstanceOf[HTMLElement]

    val h3 = document.createElement("h3").asInstanceOf[HTMLElement]
    h3.textContent = "Composition"
    section.appendChild(h3)

    val selectedTeamOpt =
      state.selectedTeam.flatMap(id => state.teams.find(_.id == id))

    if selectedTeamOpt.isEmpty then
      val p = document.createElement("p").asInstanceOf[HTMLElement]
      p.className = "mf-muted"
      p.textContent = "Sélectionne une équipe pour choisir une formation."
      section.appendChild(p)
      return section

    val team = selectedTeamOpt.get

    // déterminer la SquadSize correspondant au nbPlayers
    val maybeSize = SquadSize.values.find(_.nbPlayers == team.nbPlayers)
    val availableFormations =
      maybeSize match
        case Some(sz) => Formation.all.filter(_.taille == sz)
        case None     => Nil

    val lineupOpt =
      state.currentLineup match
        case Some(lu) if lu.teamId == team.id => Some(lu)
        case _                               => None

    // header avec select de formation
    val header = document.createElement("div").asInstanceOf[HTMLElement]
    header.className = "mf-lineup-header"

    val left = document.createElement("span").asInstanceOf[HTMLElement]
    left.className = "mf-muted"
    left.textContent = s"Équipe: ${team.name}"

    val right = document.createElement("div").asInstanceOf[HTMLElement]

    if availableFormations.nonEmpty then
      val select = document.createElement("select").asInstanceOf[HTMLSelectElement]
      select.className = "mf-select"

      for f <- availableFormations do
        val opt = document.createElement("option")
        opt.setAttribute("value", f.id.value)
        opt.textContent = f.label
        if lineupOpt.exists(_.formation.id == f.id) then
          opt.setAttribute("selected", "selected")
        select.appendChild(opt)

      select.addEventListener(
        "change",
        (_: dom.Event) =>
          val fid = FormationId(select.value)
          send(ClientToServer.SetFormation(team.id, fid))
      )

      right.appendChild(select)
    else
      val info = document.createElement("span").asInstanceOf[HTMLElement]
      info.className = "mf-muted"
      info.textContent = s"Aucune formation pour ${team.nbPlayers} joueurs."
      right.appendChild(info)

    header.appendChild(left)
    header.appendChild(right)
    section.appendChild(header)

        lineupOpt match
      case None =>
        val p = document.createElement("p").asInstanceOf[HTMLElement]
        p.className = "mf-muted"
        if availableFormations.nonEmpty@@ then
          p.textContent = "Choisis une formation pour générer la composition."
        else
          p.textContent = "Impossible de générer une compo sans formation."
        section.appendChild(p)

      case Some(lineup) =>
        val info = document.createElement("p").asInstanceOf[HTMLElement]
        info.className = "mf-muted"
        info.textContent =
          s"Formation: ${lineup.formation.label} – ${lineup.slots.size} postes."
        section.appendChild(info)

        val pitch = document.createElement("div").asInstanceOf[HTMLElement]
        pitch.className = "mf-pitch"

        def makeRow(line: Line): HTMLElement =
          val row = document.createElement("div").asInstanceOf[HTMLElement]
          row.className = "mf-pitch-row"
          val slotsForLine =
            lineup.slots.filter(_.line == line).sortBy(_.indexInLine)
          for slot <- slotsForLine do
            row.appendChild(renderPitchSlot(slot, team))
          row

        // ordre: attaque en haut, puis milieux, défense, gardien en bas
        pitch.appendChild(makeRow(Line.Attack))
        pitch.appendChild(makeRow(Line.Midfield))
        pitch.appendChild(makeRow(Line.Defense))
        pitch.appendChild(makeRow(Line.Goalkeeper))

        section.appendChild(pitch)

    section

  // applique une action et rerender
  private def send(msg: ClientToServer): Unit =
    Logic.update(state, msg) match
      case ServerToClient.StateUpdated(newState) =>
        state = newState
        render()
      case ServerToClient.Error(message) =>
        dom.window.alert(message)

```


#### Short summary: 

empty definition using pc, found symbol in pc: nonEmpty.