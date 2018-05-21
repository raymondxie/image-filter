## A. Requirement

For this problem, pseudo code is OK, or pick a language.

Your job is to take an input image (a picture) and process it with a filter to produce a modified output image.

The image is formed as follows: First 2 bytes: image width. Next 2 bytes: image height. Remaining bytes: row by row (top left to bottom right) a series of RGB pixels where each pixel is 2 bytes red, 2 bytes green, 2 bytes blue.

The  filter consists of three 3x3 arrays of numbers (one for red, one for green, one for blue).

Each pixel in the output image is formed by multiplying neighbor pixels with filter values.

For example, if the red filter is [[0.1, 0, 0], [0, 0, 0], [0, 0, 0.2]] then the red value for each pixel in the output image would be formed by multiplying 0.1 times the red value from the pixel above and left, multiplying 0.2 times the red value from the pixel below and right, then adding them.


## B. Generalization / Abstraction

The particular task is very specific in processing one type of image in a specific format.

If we give a little bit more of thought, we may create a code base that can be adapted to process additional types of image content, pixel definition, or filter pattern, etc.
We can use interface or base class to do such generalization, and leave room for extension. 
Or we can develop our code in a "generic" way, so if you supply a different type image content or filter, just tell it through a properties file, and the processor will execute accordingly.

For example, by extracting image content meta information from data stream, we can handle more image content: 
*ContentDataFormat*

````
width                   (offset, length)    => integer,  1024
height                  (offset, length)    => integer,  960
content_start_point     (offset)            => integer,  10  
pixel_length            (length_in_bytes)   => integer,  4 
````

The same applies to pixel definition (e.g. 4 bytes of RGBA for .jpg), and in our example, we use (6 bytes of RGB with each color as 2 bytes)
*PixelDataFormat*

    length		- how many bytes to represent a pixel		=> integer, e.g. 1, 2, 4, 6, 8
    parts		- how many parts of information in a pixel	=> integer, e.g  3 for RGB, 4 for RGBA, 3 for HSV, 4 for CYMK
    part-length	- how many bits for each part definition	=> integer, in our example, R has 16 bits. 

Of course, filter pattern can be defined, so we use that as a mask to pick out corresponding pixels (relative) for filter action on a particular pixel (target pixel).

Also the image can be from a file, or data stream over another channel, etc.


## C. Specifics of task

For this particular exercise, I intend to code a "generic way" so we may supply different definition properties in the future for different type of images.

Image data stream input:

````
_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _  
w   h   r   g   b   r   g   b   r   g   b  
````

Pixel data represented in 2D view

    w x h matrix	=> pixel[w][h], where each pixel holds 6 bytes of data as byte[6]

filter pattern

    3 x 3 matrix	=>	float[3][3];

when processing pixel at position p(i,j), we apply the filter pattern to the neighboring pixels at:
````
p(i-1, j-1), p(i, j-1), p(i+1, j-1)
p(i-1, j),   p(i, j),   p(i+1, j-1)
p(i-1, j+1), p(i, j+1), p(i+1, j+1)
````

The result target pixel: 

    t(i,j) = sum( f(i,j) * p(i,j) ), where for f and p observing i,j in (-1,0,1) 


## D. Processing

We need to read sufficient number pixels (at least 9 pixels) in order for applying filter pattern on corresponding pixels.

1. read entire image into memory, and process by "walking" the filter through;
2. read only 3 lines (the filter pattern height) into memory, and just "walking" that line; when the line is finished, read next line of image data to replace the outgoing line;
3. just read in the required 9 pixels (as defined by filter pattern)

Space requirement for above options are: O(WxH), O(Wx3)=O(W), and O(1). 
Depending on language chosen, or the platform on which we run the processing, option 3 "read" is frequent non-buffered read, which may have time penalty.

For this exercise, I choose to use option 2, keep 3 rotating lines in memory for coding simplicity. 

Due to each pixel processing involves neighboring pixels, we can not write target value "in-place", as that could affect some pixel processing in future.
So target data output will be in a separate line. And the space usage is in fact O(Wx4), which is still O(W).


There are two options of applying 3x3 filter:

1. take 9 pixels from source data, and apply filter to produce target data for 1 pixel (at the center);
2. take 1 pixel from source data, and apply filter to produce "portion" of target data for 9 pixels. 

Either approach will use the same amount of memory footprint if we keep 3 rows of data (either as source or target), 
and 1 row of data (either as target or source).  In my implementation, I choose 3 rows of source + 1 row of target,
as it seems easier to get head around.


## E. Corner Cases

1. For edges and corners, with filter center on those pixels, some portion of filter are out of boundary of image. Need to handle them accordingly at proper time.

2. each color has 2 bytes, with filter collecting neighboring pixels, the sum of the filtering result could exceed the value that 2 bytes can hold. 

We can either cap the value at the pixel by simply replacing it with max-value of 2 bytes; or do proportionally scale back.


## F. Java Implementation

I choose Java language for the implementation, with several classes to represents different pieces and their interaction, 
in additional to the actual data processing.

Depends on which language to use, there are different ways of addressing and accessing data elements, and applying calculation.
So it may look different if trying to utilize the capability from the language.

For example, Java has some packages in java.awt.image, and java 2D graphics that would provide convenience of processing
image data, and it would hide the details of actual algorithms and would look really simple.

But for this exercise, we want to illustrate the all the parts involved and the actual processing - kinda of "manual" approach.
So I used basic language feature and skipped all the "advanced" packages, and write out the actual processing steps, 
just like pseudocode would do to explain the processing.



If you have additional ideas or concerns, please bring them up and we can discuss!

Raymond Xie
May 20, 2018


