# This layer provides support for LORX One Wifx gateway

## Supported SoCs / MACHINE names
-   LORIX One product / sama5d4-lorix-one

## Sources

-   meta-wifx URI: git://github.com/Wifx/meta-wifx.git URI:
    https://github.com/Wifx/meta-wifx.git Branch: krogoth

## Dependencies

This Layer depends on :

-   meta-openembedded URI: git://git.openembedded.org/meta-openembedded URI:
    http://cgit.openembedded.org/meta-openembedded/ Branch: krogoth

-   meta-golang URI: git://github.com/Wifx/meta-golang.git URI:
    https://github.com/Wifx/meta-golang.git Branch: master

-   meta-maker URI: git://git.yoctoproject.org/meta-maker URI:
    https://git.yoctoproject.org/git/meta-maker Branch: master

## Build procedure

1.  Install the required packages to use Yocto as described on the [Yocto required packages page](https://www.yoctoproject.org/docs/1.8/ref-manual/ref-manual.html#required-packages-for-the-host-development-system)

    -   Ubuntu
        ```
        $ sudo apt-get install gawk wget git-core diffstat unzip texinfo \
        gcc-multilib build-essential chrpath socat
        ```

    -   Fedora (not tested)
        ```
        $ sudo yum install gawk make wget tar bzip2 gzip python unzip perl patch \
             diffutils diffstat git cpp gcc gcc-c++ glibc-devel texinfo chrpath \
             ccache perl-Data-Dumper perl-Text-ParseWords perl-Thread-Queue socat \
             findutils which
        ```

2.  Create a directory
    ```
    mkdir my_dir cd my_dir
    ```

3.  Clone yocto/poky git repository with the proper branch ready
    ```
    git clone git://git.yoctoproject.org/poky -b krogoth
    ```

4.  Clone meta-openembedded git repository with the proper branch ready
    ```
    git clone git://git.openembedded.org/meta-openembedded -b krogoth
    ```

5.  Clone meta-wifx layer with the proper branch ready
    ```
    git clone git://github.com/Wifx/meta-wifx.git -b krogoth
    ```

6.  Clone meta-golang layer with the proper branch into "golang" directory
    ```
    git clone git://github.com/Wifx/meta-golang.git golang/meta-golang -b master
    ```

7.  Clone meta-maker layer with the proper branch ready
    ```
    git clone git://git.yoctoproject.org/meta-maker
    cd meta-maker 
    git reset --hard c039fafa7a0276769d0928d16bdacd2012f2aff6 
    cd ..
    ```

8.  Enter the poky directory to configure the build system and start the build
process 
    ```
    cd poky
    ```

9.  Initialize build directory
    ```
    source oe-init-build-env build-wifx
    ```

    In case of using the SD Card version next to the NAND one, an advice is to use
two build directories, for example build-wifx and buils-wifx-sd to avoid
recompiling every machine specific packages when swithing from one version to
the other.

10. Add meta-atmel layer to bblayer configuration file
    ```
    vim conf/bblayers.conf
    ```
    With the following content:
    ```
    # POKY_BBLAYERS_CONF_VERSION is increased each time build/conf/bblayers.conf
    # changes incompatibly

    POKY_BBLAYERS_CONF_VERSION = "2"

    BBPATH = "\${TOPDIR}" BBFILES ?= ""

    BSPDIR := "\${@os.path.abspath(os.path.dirname(d.getVar('FILE', True)) +
    '/../../..')}"

    BBLAYERS ?= "  
    \${BSPDIR}/meta-wifx  
    \${BSPDIR}/poky/meta  
    \${BSPDIR}/poky/meta-poky  
    \${BSPDIR}/poky/meta-yocto-bsp  
    \${BSPDIR}/meta-openembedded/meta-oe  
    \${BSPDIR}/meta-openembedded/meta-networking  
    \${BSPDIR}/meta-openembedded/meta-perl  
    \${BSPDIR}/meta-openembedded/meta-python  
    \${BSPDIR}/meta-openembedded/meta-ruby  
    \${BSPDIR}/meta-openembedded/meta-multimedia  
    \${BSPDIR}/golang/meta-golang  
    \${BSPDIR}/meta-maker  
    "

    BBLAYERS_NON_REMOVABLE ?= "  
    \${BSPDIR}/poky/meta  
    \${BSPDIR}/poky/meta-poky  
    "
    ```

11. Edit local.conf to specify the machine, location of source archived, package
type (rpm, deb or ipk).\
    Pick one MACHINE name from the "Supported SoCs / MACHINE
names" chapter above and edit the "local.conf" file. \
    Here is an example:
    ```
    vim conf/local.conf
    ```
    And the content:
    ```
    [...]
    # LORIX One NAND memory based
    MACHINE ??= "sama5d4-lorix-one"
    or
    # LORIX One SD-Card memory based
    MACHINE ??= "sama5d4-lorix-one-sd"
    [...]
    DL_DIR ?= "your_download_directory_path"
    [...]
    PACKAGE_CLASSES ?= "package_ipk"
    [...]
    EXTRA_IMAGE_FEATURES ?= ""
    [...]
    USER_CLASSES ?= "buildstats image-mklibs"
    ```

    To get better performance, use the "poky-wifx" distribution by also adding that
line:
    ```
    DISTRO = "poky-wifx"
    ```

    To get build history (use more space on disk), activate the option with this
line in end of file:
    ```
    INHERIT += "buildhistory"
    BUILDHISTORY_COMMIT = "1"
    ```

    To remove work files after the build system has finished and reduce the overall system size, activate the option with this line in end of file:
    ```
    INHERIT += "rm_work"
    ```

12. Build Wifx standard image
    ```
    $ bitbake wifx-base
    ```

    Typical bitbake output
    ```
    Build Configuration:
    BB_VERSION        = "1.30.0"
    BUILD_SYS         = "x86_64-linux"
    NATIVELSBSTRING   = "universal"
    TARGET_SYS        = "arm-poky-linux-gnueabi"
    MACHINE           = "sama5d4-lorix-one"
    DISTRO            = "poky-wifx"
    DISTRO_VERSION    = "2.1.2"
    TUNE_FEATURES     = "arm armv7a vfp thumb neon       callconvention-hard       cortexa5"
    TARGET_FPU        = "hard"
    meta-wifx         = "krogoth:f5e1a500096c1a4ffa8b77657ad553f102bfa986"
    meta              
    meta-poky         
    meta-yocto-bsp    = "krogoth:ae9b341ecfcc60e970f29cfe04306411ad26c0cf"
    meta-oe           
    meta-networking   
    meta-perl         
    meta-python       
    meta-ruby         
    meta-multimedia   = "krogoth:55c8a76da5dc099a7bc3838495c672140cedb78e"
    meta-golang       = "master:72e26c77e91311e79f38863424adca9536a2bde6"
    meta-maker        = "master:c039fafa7a0276769d0928d16bdacd2012f2aff6"
    ```

13. Or build the more complete image include for example OpenVPN and other
network features
    ```
    bitbake wifx-extended
    ```

14. Or build the debug image which allow you for example to use the root user
without password (don't use it in production)
    ```
    bitbake wifx-base-dbg
    ```

15. Or build the devel image which contains also the toolchain and app headers
    ```
    bitbake wifx-base-dev
    ```

16. Or build the SDK image which contains in addition the kernel headers and
debug utilities
    ```
    bitbake wifx-base-sdk
    ```

17. In all cases, the toolchain installer is located in
build-wifx/tmp/deploy/sdk

Maintainers: Yannick Lanz <yannick.lanz@wifx.com>

When creating patches insert the [meta-wifx] tag in the subject, for example use
something like: git format-patch -s --subject-prefix='meta-wifx][PATCH'
