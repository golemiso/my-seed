package controllers

import org.scalatestplus.play.BaseOneAppPerTest
import org.scalatest.FunSpec
import play.api.test.FakeRequest
import config.MacWireApplicationFactory
import play.api.libs.json.Json
import play.api.test.Helpers._

class PlayerControllerSpec extends FunSpec with BaseOneAppPerTest with MacWireApplicationFactory {
  describe("PlayerController") {
    describe("post") {
      describe("id unspecified") {
        it("200") {
          val Some(result) = route(app, FakeRequest(POST, "/players").withBody(Json.toJson(PlayerResource(None, "Test User"))))
          assert(status(result) === CREATED)
        }
      }
      describe("id specified") {
        it("200") {
          val Some(result) = route(app, FakeRequest(POST, "/players").withBody(Json.toJson(PlayerResource(Some("00000000-0000-0000-0000-000000000000"), "Test User"))))
          assert(status(result) === CREATED)
        }
      }
    }

    describe("getAll") {
      it("200") {
        val Some(result) = route(app, FakeRequest(GET, "/players"))
        assert(status(result) === OK)
        val Some(players) = Json.fromJson[Seq[PlayerResource]](contentAsJson(result)).asOpt
        assert(players.head.name === "Test User")
      }
    }

    describe("get") {
      it("200") {
        val Some(result) = route(app, FakeRequest(GET, "/players/00000000-0000-0000-0000-000000000000"))
        assert(status(result) === OK)
        val Some(player) = Json.fromJson[PlayerResource](contentAsJson(result)).asOpt
        assert(player.name === "Test User")
      }
    }
  }
}
