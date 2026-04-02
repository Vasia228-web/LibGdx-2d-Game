<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.11.2" name="objects" tilewidth="80" tileheight="112" tilecount="3" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="0" type="Prop">
  <properties>
   <property name="atlasAsset" value="OBJECTS"/>
  </properties>
  <image source="../../../assets_raw/objects/house/house.png" width="80" height="112"/>
 </tile>
 <tile id="1" type="Prop">
  <properties>
   <property name="atlasAsset" value="OBJECTS"/>
  </properties>
  <image source="../../../assets_raw/objects/oak_tree/oak_tree.png" width="41" height="63"/>
 </tile>
 <tile id="2" type="GameObject">
  <properties>
   <property name="animation" value="IDLE"/>
   <property name="animationSpeed" type="float" value="1"/>
   <property name="atlasAsset" value="OBJECTS"/>
   <property name="controller" type="bool" value="true"/>
   <property name="speed" type="float" value="4"/>
  </properties>
  <image source="../../../assets_raw/objects/player/player.png" width="32" height="32"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="11.9806" y="17.6471" width="3.02213">
    <ellipse/>
   </object>
   <object id="2" x="11.1156" y="18.0178" width="8.81283" height="5.24678">
    <ellipse/>
   </object>
  </objectgroup>
 </tile>
</tileset>
