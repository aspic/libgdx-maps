# An OpenStreetMap widget for libgdx

A _work in progress_ OpenStreetMap widget pluggable into libgdx!  Parts
of this widget is based on the work done in [jxmapviewer2]
(https://github.com/msteiger/jxmapviewer2). I wanted to keep the
dependencies down to a minimum, so I re-used/copied some of those
utilities into this project.

This widget is currently very bare bone (and probably broken in several
ways). It fetches tiles as textures from the configured back end, and
stores these tiles as a TiledMapLayer, based on their zoom level.

![Map teaser](http://mehl.no/maps/teaser.png "teaser!")

## Goal

The goal of this project is to create a cross platform OpenStreetMap
module for libgdx applications. It should be easy to switch between OSM
back ends (such as openstreetmap.org and mapquest.co.uk).

## Usage

### Scene2D

First of all a MapManager has to be created. This is the main class
encapsulating the camera, framebuffer object and network logic. Next
pass this manager to the MapWidget. Then add the widget to your stage.

    ...
    mapManager = new MapManager(new MapQuestTileFactoryInfo());

    MapWidget widget = new MapWidget(manager);
    widget.setSize(stage.getWidth(), stage.getHeight());
    stage.addActor(widget);
    ...

The widget inherits from Table which makes it easy to add it to other
widgets (or to add widgets to the map). Below is an image of a map added
to a Table in Scene2d.

![Scene2d map](http://mehl.no/maps/scene2d-maps.png "scene2d!")

### Manually

The map can also be rendered without scene2d. Create the manager and
render the map by:

    render() {
        // Fetches tiles and render the FBO
        mapManager.update();
        // Use spritebatch to render the FBO as a texture
        batch.begin();
        batch.draw(mapManager.getMapTexture(), 0, 0);
        batch.end();
    }

## Dependencies

This project depends on [libgdx](https://github.com/libgdx/). You will
have to add the latest gdx.jar to your classpath in order to compile and
run the example application.
