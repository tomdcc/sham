# Automated Testing

If you wish to use Sham in your data fixtures, you may be concerned about using
it if those fixtures get used by automated tests. There's nothing worse than a
broken automated build that you can't reproduce, and you may have trouble
reproducing a failed build if your data is all random. Happily, Sham has a
solution.

Sham uses a single internal random number generator as the source of its
randomness. Therefore, as long as the RNG can be reset to the state that it was
in at the start of your test, you can get Sham to recreate the same data that
was generated in the test.

Simply log the random seed that Sham returns from calls to `getSeed()` at the
start of your tests, perhaps to a log file which your build infrastructure
keeps as a build artifact, and you can then set the seed, either by a direct
call to Sham's `setSeed()` method, or by setting the `sham.seed` system
property. When that property is set, Sham will initialize its random number
generator to he specified value, and your test (as long as it is run before
anything else calls Sham) will get the same data that was generated during
the automated build.

This is why it is important that any custom generators are registered with
Sham via the `registerGenerator()` method, and use the RNG that Sham gives
them - if they do not, their data will not be reproducable in the same way.

Note that the getting and setting of the random number generator's internal
seed requires the use of reflection to get at its internal state - this
may not be allowed if you are running inside a `SecurityManager` as is the
case in some servlet and J2EE containers, and may not be portable across
different JRE versions. Currently it has been tested in JDK 1.6 on Linux
and OSX - if it does not work for you or you get any errors, please report
your JDK / JRE version, platform and the error that you received.
