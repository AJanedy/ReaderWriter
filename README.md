#
# Product written and submitted by ANDREW JANEDY
# November 2024
#

# ================================================================
#
# ReaderWriter demonstrates a simulated system where readers and
# writers, through mutual exclusion and synchronization, access a
# shared data point.  Dual semaphores ensure writers have priority
# over readers and that readers can only access the database when
# no writers are accessing it.
# 
# Critical section exclusivity is managed by mutex (line 8), while
# priority to writers is managed by writePriority (line 9).
#
# This program is called with 3 arguments, <# of readers> 
# <# of writers> <# of access>.  Readers are given names A, B, C,
# etc... and writers are given names F, G, H, etc...
#
# =================================================================
#
# When a reader arrives and acquires the mutex, readerCount is 
# incremented to indicate that a new thread is accessing the 
# database (line 31).  If it is the first reader, it acquires the 
# writePriority semaphore (line 36), which excludes writers from 
# the database.  Reader then releases the mutex allowing other 
# readers access, and as long as there are readers, writePriority 
# will prevent a writer from accessing the shared data.  
#
# After the reader accesses the database, it acquires the mutex 
# again, and again decrements readerCount (line 45).  If, at this
# point, readerCount == 0, then the writePriority semaphore is 
# released. Reader again releases the mutex.
# 
# =================================================================
#
# When a writer arrives it acquires the writePriority mutex 
# (line 78).  This prevents any other writer or reader from 
# accessing the shared data space.  The writer then rewrites 
# database (line 83) and releases the writePriority semaphore 
# (line 87).
#
# =================================================================

# Compile: javac ReaderWriter.java 
# Run: javac ReaderWriter <numReaders><numWriters><numAccesses>