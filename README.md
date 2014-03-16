# An OpenStreetMap widget for libgdx

A _work in progress_ OpenStreetMap widget pluggable into libgdx!  Parts
of this widget is based on the work done in [jxmapviewer2]
(https://github.com/msteiger/jxmapviewer2). I wanted to keep the
dependencies down to a minimum, so I re-used/copied some of those
utilities into this project.

This widget is currently very bare bone (and probably broken in several
ways). It fetches tiles as textures from the configured back end, and
stores these tiles as a TiledMapLayer, based on their zoom level.

## Goal

The goal of this project is to create a cross platform OpenStreetMap
module for libgdx applications. It should be easy to switch between OSM
back ends (such as openstreetmap.org and mapquest.co.uk).

## Dependencies

This project depends on [libgdx](https://github.com/libgdx/). You will
have to add the latest gdx.jar to your classpath in order to compile and
run the example application.
