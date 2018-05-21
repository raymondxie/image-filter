# Image Filter Processing

#### An exercise to design and explain how to apply 3x3 filter to an image.

## Design
Please [check the thought process](../master/design-thinking.txt) of this design and implementation.

## Implementation

1. **Processor** class

The main driver to start the filter process.

2. **RGBFileTransform** class

The main engine that contains processing logic. 
It has access to source image stream, and target image stream for filtered results. It takes a filter to carry out the filter processing.

3. **RGBFilter** class

The definition of actual filter for all color channels.

4. **RGBPixel** class

The definition of each pixel in the image stream, such as how many bytes per pixel, and how many bits for each color channel.

5. **BaseConfig** class

A base class to read definition from properties file.
Both RGBFilter, RGBPixel, and image format in stream (TODO) are based on this class for getting definition.

## Invocation
For this implementation, it takes an image as input file, and produces filter result in an output file.
> java Processor _input-image_ _output-image_

