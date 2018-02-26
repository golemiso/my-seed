package domain

import java.util.UUID

case class Team(id: TeamID, players: Seq[Player]) extends Entity[TeamID]

case class TeamID(value: UUID) extends Identity
