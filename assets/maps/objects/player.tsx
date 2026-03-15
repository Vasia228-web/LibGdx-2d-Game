<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.11.2" name="objects" tilewidth="80" tileheight="112" tilecount="3" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="0">
  <properties>
   <property name="atlasRegion" value="house/house"/>
  </properties>
  <image source="../../../assets_raw/objects/house/house.png" width="80" height="112"/>
 </tile>
 <tile id="1">
  <properties>
   <property name="atlasRegion" value="oak_tree/oak_tree"/>
  </properties>
  <image source="../../../assets_raw/objects/oak_tree/oak_tree.png" width="41" height="63"/>
 </tile>
 <tile id="2">
  <properties>
   <property name="atlasRegion" value="player/player"/>
   <property name="life" type="float" value="6"/>
   <property name="speed" type="float" value="2"/>
  </properties>
  <image source="../../../assets_raw/objects/player/player.png" width="32" height="32"/>
 </tile>
</tileset>
