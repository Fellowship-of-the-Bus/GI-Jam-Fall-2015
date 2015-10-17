package com.github.fellowship_of_the_bus
package eshe
package game
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException,Color, Input}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

import IDMap._
import lib.game.GameConfig.{Height,Width}

class Game extends lib.game.Game {
  var counter = 0

  val maxPlayers = 4
  val players = new Array[Player](maxPlayers)
  for (i <- 0 until maxPlayers) {
    players(i) = new IVGuy(0, 0)
  }

  var projectiles = List[Projectile]()
  var enemies = List[Enemy](new Ghost(1000, 0), new Ghost(1000, 400))

  var cleanUpPeriod = 120
  var timer = 0
  def cleanup() = {
    enemies = enemies.filter(_.active)
    projectiles = projectiles.filter(_.active)
  }

  def update(gc: GameContainer, game: StateBasedGame, delta: Int) = {
    implicit val input = gc.getInput
    if (timer == 0) {
      cleanup
      timer = cleanUpPeriod
    } else {
       timer -= 1
    }

    for (e <- enemies; if (e.active)) {
      e.move
      e.update(delta)
    }

    for (p <- projectiles; if (p.active)) {
      p.update(delta)
    }

    for (p <- players; if (p.active)) {
      p.update(delta)
      if (p.hp < 0) {
        p.hp = 0
        p.inactivate
      }
    }
  }
}
