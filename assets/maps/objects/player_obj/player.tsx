<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.11.2" name="player_obj" tilewidth="12" tileheight="26" tilecount="1" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="0">
  <properties>
   <property name="animation" value="IDLE"/>
   <property name="animationSpeed" type="float" value="1"/>
   <property name="atlasAsset" value="OBJECTS"/>
   <property name="speed" type="float" value="4"/>
  </properties>
  <image source="../../../../assets_raw/objects/player/player.png" width="12" height="26"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="4.79465" y="14.3841" width="2.74151" height="2.19409">
    <ellipse/>
   </object>
   <object id="2" x="5.17666" y="11.9482" width="1.82774" height="2.4096"/>
  </objectgroup>
 </tile>
</tileset>
