# An OpenStreetMap widget for [Libgdx](https://github.com/libgdx/libgdx)

A _work in progress_ OpenStreetMap widget pluggable into libgdx!  Parts
of this widget is based on the work done in [jxmapviewer2]
(https://github.com/msteiger/jxmapviewer2). I wanted to keep the
dependencies down to a minimum, so I re-used/copied some of those
utilities into this project.

This widget is currently bare bone (and probably broken in several
ways). It fetches tiles as textures from the configured back end, and
stores these tiles as a TiledMapLayer, based on their zoom level.

![Map teaser](http://mehl.no/maps/teaser.png "teaser!")

## Goal

The goal of this project is to create a cross platform OpenStreetMap
module for libgdx applications. It should be easy to switch between OSM
back ends (such as openstreetmap.org and mapquest.co.uk).

## Usage

All interaction with the map is done through the _MapManager_. This
class encapsulates the camera, a framebuffer object and the network
logic.

The _MapManager_ has to be instantiated with a map back end. These
back ends describe where the map manager should fetch tiles from. The
simplest way to create a _MapManager_ is by using one of the predefined
map back ends:

    MapManager manager = MapManager(new CloudmadeTileFactoryInfo());

This is all there is to it, and the map representation is stored in a
texture in the manager. This texture can be retrieved by calling:

    manager.getMapTexture();

In order to update this texture (ensure that it gets redrawn on map
updates) call update() in your render or update loop:

    manager.update();

### Scene2D

Simply create a new MapWidget with the MapManager as an argument. This
widget is basically a texture encapsulated by a Table.

    ...
    mapManager = new MapManager(new MapQuestTileFactoryInfo());

    MapWidget widget = new MapWidget(manager);
    widget.setSize(stage.getWidth(), stage.getHeight());
    stage.addActor(widget);
    ...

The widget inherits from Table which makes it easy to add it to other
widgets (or to add widgets to the map). Below is an image of a map added
to a Table in Scene2d.

<p align="center">
    <img src="http://mehl.no/maps/scene2d-maps2.png" alt="Scene2D!" />
</p>

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
