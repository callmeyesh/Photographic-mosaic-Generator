#!/bin/bash


TILE=$1
ROW=$2
COLUMN=$3
DESTINATION=$4

echo $ROW $COLUMN $DESTINATION
/usr/bin/montage $TILE -tile $ROWx$COLUMN -geometry +0+0 -background none $DESTINATION
