#!/bin/sh

#setup parameters
PACKAGE_NAME="translation-recorder"
PACKAGE_VERSION="0.0"
SOURCE_DIR=$PWD
TEMP_DIR="/tmp"

#make the correct directory structure
mkdir -p $TEMP_DIR/debian/DEBIAN
mkdir -p $TEMP_DIR/debian/lib
mkdir -p $TEMP_DIR/debian/lib
mkdir -p $TEMP_DIR/usr/share/applications
mkdir -p $TEMP_DIR/usr/share/doc/$PACKAGE_NAME

#add content to the control file
echo "Package: $PACKAGE_NAME" > $TEMP_DIR/debian/DEBIAN/control
echo "Version: $PACKAGE_VERSION" >> $TEMP_DIR/debian/DEBIAN/control
cat debian/control >> $TEMP_DIR/debian/DEBIAN/control

#copy desktop, copyright, and executable files to their respective proper locations
cp debian/translation-recorder.desktop $TEMP_DIR/debian/usr/share/applications/
cp debian/copyright $TEMP_DIR/debian/usr/share/doc/$PACKAGE_NAME/
cp -r ../build/libs $TEMP_DIR/debian/lib/$PACKAGE_NAME

#compress the changelog file and place in the doc section of the package
gzip -9c $TEMP_DIR/debian/lib/$PACKAGE_NAME/NEWS > $TEMP_DIR/debian/usr/share/doc/$PACKAGE_NAME/changelog.gz

#move the svg icon to its proper location and give it the correct permissions as enforced by Debian
mv $TEMP_DIR/debian/lib/$PACKAGE_NAME/resources/translation-recorder.svg $TEMP_DIR/debian/usr/share/doc/$PACKAGE_NAME/
chmod 644 $TEMP_DIR/debian/usr/share/doc/$PACKAGE_NAME/translation-recorder.svg

#remove unnecessary files
rm -r $TEMP_DIR/debian/lib/$PACKAGE_NAME/debian
rm $TEMP_DIR/debian/lib/$PACKAGE_NAME/COPYING
rm $TEMP_DIR/debian/lib/$PACKAGE_NAME/packagedeb.sh

#create shell wrapper for calling jar executable
echo '#!/bin/sh' > $TEMP_DIR/debian/bin/translation-recorder
echo "export CLASSPATH=$CLASSPATH:/lib/$PACKAGE_NAME" >> $TEMP_DIR/debian/bin/translation-recorder
echo "java -jar 8woc2018-jvm.jar"
chmod 755 $TEMP_DIR/debian/bin/translation-recorder

#determine size of package in bytes and add it into the control file
PACKAGE_SIZE=`du -bs $TEMP_DIR/debian | cut -f 1`
PACKAGE_SIZE=$((PACKAGE_SIZE/1024))
echo "Installed-Size: $PACKAGE_SIZE" >> $TEMP_DIR/debian/control

#give root ownership over the program so it is installed across the entire system
chown -R root $TEMP_DIR/debian
chown -R root $TEMP_DIR/debian

#build the package
cd $TEMP_DIR/
dbpkg --build debian
mv debian.deb $SOURCE_DIR/$PACKAGE_NAME-$PACKAGE_VERSION.deb
rm -r $TEMP_DIR/debian