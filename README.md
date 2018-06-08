# Marlo
[![license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/nectec-opensource/marlo.svg?branch=master)](https://travis-ci.org/nectec-opensource/marlo)
[![Download](https://api.bintray.com/packages/nectec-wisru/maven/Marlo/images/download.svg)](https://bintray.com/nectec-wisru/maven/Marlo/_latestVersion)

Create awesome android application with mark location feature on google's map by lazy

![Marlo Screenshot][screenshot]

## Feautre
- `PolygonMarloFragment` for create Polygon geometry
- `PointMarloFragment` for mark one to multi point geometry
- Swicth between maps and satellite mode
- Mark with sound effect (you can also mute it)
- Support data flow with GeoJson or Marlo's model
- Build-in drawing tools that can styles as you want also can padding top or Bottom
- Enable Location feature if not
- Auto start maps at current location or specify location by lazy
- `MarloFragment` is open for extension 

## Download

make sure add JCenter to build script's repositories

```groovy
repositories {
    jcenter()
}
```

Add dependencies on app module

```groovy
dependencies {
    ...
    compile 'th.or.nectec.android:marlo:+' //Change `+` to latest stable version is Recommended
    ...
}
```

## Library User

- [FAARMis](https://play.google.com/store/apps/details?id=th.in.faarmis)

## License

    Copyright 2016 NECTEC
      National Electronics and Computer Technology Center, Thailand

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


[![NECTEC](http://www.nectec.or.th/themes/nectec/img/logo.png)](https://www.nectec.or.th)

[screenshot]: https://github.com/nectec-opensource/marlo/blob/master/asset/screenshot.webp?raw=true "screenshot"
