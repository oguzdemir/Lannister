#!/bin/bash -e

cd ${0%\\*}

echo ""
echo "Starting set-up. Inspect for any errors. Script ends successfully with 'Done.'"
echo "Current directory is $(pwd)"
echo "Cleaning up existing set-up..."

rm -Rf ../massim-2013-1.4/
rm -f massim-2013-1.4-bin.zip

echo "Downloading massim archive..."
curl -L -silent -o massim-2013-1.4-bin.zip --max-time 60 http://bogdan.sorlea.com/massim-2013-1.4-bin.zip

echo "Download complete. Extracting..."
unzip massim-2013-1.4-bin.zip 1>/dev/null
mv -f massim-2013-1.4 ../

echo "Extract complete. Cleaning up the scene..."
rm -f ../massim-2013-1.4/eismassim/eismassimconfig.xml
rm -Rf ../massim-2013-1.4/javaagents
rm -Rf ../massim-2013-1.4/webapp
rm -f ../massim-2013-1.4/CHANGELOG
rm -f ../massim-2013-1.4/massim/scripts/conf/*.xml

echo "Putting server/simulation configuration in place..."
set +e
cp -f ../server_config/*.xml ../massim-2013-1.4/massim/scripts/conf/ 2>/dev/null
cp -f ../server_config/helpers/2013/*.xml ../massim-2013-1.4/massim/scripts/conf/helpers/2013 2>/dev/null
set -e

echo "Updating java (eclipse) classpath..."
COUNT=$(grep '${MASSIM_WORKSPACE}' ../.classpath | wc -l)
WORKSPACE_DIR=$(sh -c 'cd ..; pwd -W')
echo "Workspace dir is $WORKSPACE_DIR"
if [ $COUNT -ge 1 ]; then
  echo "Found $COUNT classpath occurences. Refresh Eclipse if needed."
  sed -i "s@\${MASSIM_WORKSPACE}@$WORKSPACE_DIR@g" ../.classpath
else
  echo "No classpath updates needed."
fi

echo "Cleaning up..."
rm -f massim-2013-1.4-bin.zip

echo "Done."