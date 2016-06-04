package com.github.fellowship_of_the_bus
package eshe
package game

import IDMap._

import org.newdawn.slick.{GameContainer, Graphics}

import lib.slick2d.ui.{Drawable}
import lib.slick2d.game.SlickGameConfig
import lib.game.GameConfig.{Width}
import lib.game.GameConfig
import lib.util.{TickTimer,TimerListener,FireN}
import lib.math.clamp

import eshe.state.ui.PlayerListener
import eshe.state.ui.{GameArea}

trait PlayerType extends CharacterType {
  val kickImage: Drawable
  val jumpImage: Drawable
}

abstract case class Player(xc: Float, yc: Float, override val base: PlayerType) extends game.Character(xc, yc, base) {
  def tryAttack(game: Game) = {

  }
  def tryAttack2(game: Game) = {

  }
  var score = 0

  override def hit(c: Character, strength: Int) = {
    val damage = strength
    c match {
      case e: Enemy => e.knockback(direction * damage * 5)
      case _ => ()
    }
    score += damage
    super.hit(c, strength)
  }

  override def move(xamt: Float, yamt: Float) = {
    super.move(xamt, yamt)
    x = clamp(x, 0, Width-width)
    y = clamp(y, GameArea.fenceHeight-height, GameArea.height-height)
  }
}

object IVGuys {
  val guys = Array(IVGuy, IVGuy2, IVGuy3, IVGuy4)
}

object IVGuy extends PlayerType {
  val id = IVGuyW1ID
  val maxHp = 100
  val attack = 30
  val defense = 20
  val speed = 5

  var walk1 = images(IVGuyW1ID)
  var walk2 = images(IVGuyW2ID)
  var imgs = Array[Drawable](walk1, walk2)

  val kickImage = images(IVGuyKickID)
  val jumpImage = images(IVGuyJumpID)

  val atkHeight = 370.0f
  val atkWidth = 60.0f

  val atkHeight2 = 390.0f
}

object IVGuy2 extends PlayerType {
  val id = IVGuy.id
  val maxHp = IVGuy.maxHp
  val attack = IVGuy.attack
  val defense = IVGuy.defense
  val speed = IVGuy.speed

  var walk1 = images(IVGuy2W1ID)
  var walk2 = images(IVGuy2W2ID)
  var imgs = Array[Drawable](walk1, walk2)

  val kickImage = images(IVGuy2KickID)
  val jumpImage = images(IVGuy2JumpID)

  val atkHeight = IVGuy.atkHeight
  val atkWidth = IVGuy.atkWidth
}

object IVGuy3 extends PlayerType {
  val id = IVGuy.id
  val maxHp = IVGuy.maxHp
  val attack = IVGuy.attack
  val defense = IVGuy.defense
  val speed = IVGuy.speed

  var walk1 = images(IVGuy3W1ID)
  var walk2 = images(IVGuy3W2ID)
  var imgs = Array[Drawable](walk1, walk2)

  val kickImage = images(IVGuy3KickID)
  val jumpImage = images(IVGuy3JumpID)

  val atkHeight = IVGuy.atkHeight
  val atkWidth = IVGuy.atkWidth
}

object IVGuy4 extends PlayerType {
  val id = IVGuy.id
  val maxHp = IVGuy.maxHp
  val attack = IVGuy.attack
  val defense = IVGuy.defense
  val speed = IVGuy.speed

  var walk1 = images(IVGuy4W1ID)
  var walk2 = images(IVGuy4W2ID)
  var imgs = Array[Drawable](walk1, walk2)

  val kickImage = images(IVGuy4KickID)
  val jumpImage = images(IVGuy4JumpID)

  val atkHeight = IVGuy.atkHeight
  val atkWidth = IVGuy.atkWidth
}

class IVGuy(xc: Float, yc: Float, playerNum: Int) extends Player(xc, yc, IVGuys.guys(playerNum)) {
  val name = "Herbert"

  def atkHeight2 = IVGuy.atkHeight2
  val guy = IVGuys.guys(playerNum)
  val armDefault = images(IVGuyArmID).copy
  val armPunch = images(IVGuyArmPunchID).copy
  val kick = guy.kickImage.copy
  val jump = guy.jumpImage.copy
//  val kick = images(IVGuyKickID).copy
//  val jump = images(IVGuyJumpID).copy
  var currArm = armDefault
  var time = 0

  val action = new TimerListener {}
  val movement = new TimerListener {}

  override def tryAttack(game: Game) = {
    // only one action at a time
    action.cancelAll()

    def resetArm() = {
      currArm = armDefault
      img = imgs(index)
    }
    resetArm
    action += new TickTimer(10, resetArm _)

    currArm = armPunch
    val scale =  state.ui.GameArea.scaleFactor
    val x1 = direction match {
      case GameObject.Right => x + (130 + 225) * scale
      case GameObject.Left => x + 90 * scale
    }
    val y1 = y + (80 + 240) * scale
    val targs = getTargets(x1, y1, x1 + 40 * scale * direction, y1 + 50 * scale, false, game)
    for (t <- targs){
      hit(t, attack)
    }
  }
  override def tryAttack2(game: Game) = {
    // only one action at a time
    action.cancelAll()
    resetArm
    action += new TickTimer(15, doKick _)
    action += new TickTimer(30, () => img = jump)
    action += new TickTimer(45, resetArm _)

    def doKick() = {
      img = kick
      val x1 = direction match {
        case GameObject.Right => x + width
        case GameObject.Left => x
      }
      val y1 = y + (atkHeight2* state.ui.GameArea.scaleFactor)
      val targs = getTargets(x1, y1, x1 - (310 * state.ui.GameArea.scaleFactor),y1 + (170 * state.ui.GameArea.scaleFactor), false, game)
      for (t <- targs) {
        hit(t, attack * 2)
      }
    }

    def resetArm() = {
      currArm = armDefault
      img = imgs(index)
    }

    img = jump
  }

  override def draw(g: Graphics, gc: GameContainer) = {
    SlickGameConfig.graphics = g
    drawScaledImage(img, x, y, g)
    if (currArm != null && img != jump && img != kick) {
      if (direction == GameObject.Left) {
        drawScaledImage(currArm, x - (currArm.getWidth - (180 * state.ui.GameArea.scaleFactor)), y + (240 * state.ui.GameArea.scaleFactor), g)
      } else {
        drawScaledImage(currArm, x + (130 * state.ui.GameArea.scaleFactor), y + (240 * state.ui.GameArea.scaleFactor), g)
      }
    }
  }

  override def update(delta: Long, game: Game) = {
    super.update(delta, game)

    // dependency between these two: some actions update the
    // movement image, so they should be updated second
    movement.tick(delta)
    action.tick(delta)
  }
}
