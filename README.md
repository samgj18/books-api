# Books API

This application is an example of how to use newtypes and refined types with http4s and circe to create a CRUD for books.

##### Is written in a pure functional way using [cats](https://typelevel.org/cats-effect/), [fs2](https://fs2.io/#/), [circe](https://circe.github.io/circe/), [derevo](https://github.com/tofu-tf/derevo), [newtypes](https://github.com/estatico/scala-newtype), [refined](https://github.com/fthomas/refined), [doobie](https://tpolecat.github.io/doobie/) and [http4s](https://github.com/http4s/http4s/blob/main/examples/blaze/src/main/scala/com/example/http4s/blaze/BlazeWebSocketExample.scala)

### How to use?

- Run ```docker-compose up -d```, it'll start the postgres database we will use to store our books.
- Run ```sbt ~reStart```, it'll spin up the server and will reload whenever a file changes.
- 

The payload to create a book is the following:
```json
{"name": "{{string}}", "author": "{{string}}", "year": "{{int}}"}
```
