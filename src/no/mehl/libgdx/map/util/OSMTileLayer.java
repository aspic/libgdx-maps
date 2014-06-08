/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package no.mehl.libgdx.map.util;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

/** @brief Layer for a TiledMap */
public class OSMTileLayer extends TiledMapTileLayer {

	private int width;
	private int height;

	private float tileWidth;
	private float tileHeight;

	private Vector2 tempPoint = new Vector2();
	private ObjectMap<Vector2, Cell> cells;

	/** @return layer's width in tiles */
	public int getWidth () {
		return width;
	}

	/** @return layer's height in tiles */
	public int getHeight () {
		return height;
	}

	/** @return tiles' width in pixels */
	public float getTileWidth () {
		return tileWidth;
	}

	/** @return tiles' height in pixels */
	public float getTileHeight () {
		return tileHeight;
	}

	/** Creates TiledMap layer
	 *
	 * @param width layer width in tiles
	 * @param height layer height in tiles
	 * @param tileWidth tile width in pixels
	 * @param tileHeight tile height in pixels */
	public OSMTileLayer(int width, int height, int tileWidth, int tileHeight) {
		super(0, 0, 0, 0);
		this.width = width;
		this.height = height;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.cells = new ObjectMap<Vector2, TiledMapTileLayer.Cell>();
	}

	/** @param x X coordinate
	 * @param y Y coordinate
	 * @return {@link Cell} at (x, y) */
	public TiledMapTileLayer.Cell getCell (int x, int y) {
		return this.cells.get(tempPoint.set(x, y));
	}

	/** Sets the {@link Cell} at the given coordinates.
	 *
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param cell the {@link Cell} to set at the given coordinates. */
	public void setCell (int x, int y, TiledMapTileLayer.Cell cell) {
		this.cells.put(new Vector2(x, y), cell);
	}
}
