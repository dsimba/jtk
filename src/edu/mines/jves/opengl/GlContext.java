/****************************************************************************
Copyright (c) 2004, Colorado School of Mines and others. All rights reserved.
This program and accompanying materials are made available under the terms of
the Common Public License - v1.0, which accompanies this distribution, and is
available at http://www.eclipse.org/legal/cpl-v10.html
****************************************************************************/
package edu.mines.jves.opengl;

import java.awt.Canvas;
import edu.mines.jves.util.Check;

/**
 * An OpenGL context. All OpenGL rendering is performed using the current
 * OpenGL context. A thread sets the current context by locking it. After
 * rendering, that thread unlocks the context. At any time, a single thread 
 * may hold the lock to no more than one context. Multiple threads cannot 
 * simultaneously hold the lock to one context. Therefore, contexts should
 * be unlocked sometime after they are locked.
 * <p>
 * To ensure that each call to {@link #lock()} is paired with a call to 
 * {@link #unlock()}, perform the latter in a finally block, as in this 
 * example:
 * <pre><code>
 *   context.lock();
 *   try {
 *     // ...
 *     // render using various OpenGL methods
 *     // ...
 *     context.swapBuffers();
 *   } finally {
 *     context.unlock();
 *   }
 * </code></pre>
 * @author Dave Hale, Colorado School of Mines
 * @version 2004.11.24
 */
public class GlContext {

  /**
   * Constructs an OpenGL context for the specified AWT canvas.
   * @param canvas the canvas.
   */
  public GlContext(java.awt.Canvas canvas) {
    _peer = makeGlAwtCanvasContext(canvas);
  }

  /**
   * Locks this context. If another thread has locked this context,
   * this method waits until that lock is released.
   * @exception IllegalStateException if the current thread already 
   *  has this or any other OpenGL context locked.
   */
  public void lock() {
    _lock.acquire();
    Check.state(Gl.getContext()==null,
      "the current thread has no OpenGL context locked");
    Gl.setContext(this);
    boolean locked = lock(_peer);
    Check.state(locked,"this OpenGL context has been locked");
    if (!_gotProcAddresses)
      getProcAddresses();
  }

  /**
   * Unlocks this context.
   * @exception IllegalStateException if the current thread does
   *  already have this OpenGL context locked.
   */
  public void unlock() {
    Check.state(Gl.getContext()==this,
      "the current thread has this OpenGL context locked");
    Gl.setContext(null);
    boolean unlocked = unlock(_peer);
    Check.state(unlocked,"this OpenGL context has been unlocked");
    _lock.release();
  }

  /**
   * Swaps the front and back buffers for this context.
   */
  public void swapBuffers() {
    Check.state(Gl.getContext()==this,
      "the current thread has this OpenGL context locked");
    swapBuffers(_peer);
  }

  /**
   * Dispose of this context.
   */
  public void dispose() {
    killGlContext(_peer);
  }

  ///////////////////////////////////////////////////////////////////////////
  // protected

  protected void finalize() throws Throwable {
    try {
      dispose();
    } finally {
      super.finalize();
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // private

  private long _peer; // C++ peer of this OpenGL context
  private ReentrantLock _lock = new ReentrantLock(); // mutual exclusion lock
  private boolean _gotProcAddresses;

  private static native void killGlContext(long peer);
  private static native long makeGlAwtCanvasContext(java.awt.Canvas canvas);
  private static native boolean lock(long peer);
  private static native boolean unlock(long peer);
  private static native boolean swapBuffers(long peer);

  ///////////////////////////////////////////////////////////////////////////
  // pointers to OpenGL functions after version 1.1

  // OpenGL 1.2
  long glBlendColor;
  long glBlendEquation;
  long glDrawRangeElements;
  long glColorTable;
  long glColorTableParameterfv;
  long glColorTableParameteriv;
  long glCopyColorTable;
  long glGetColorTable;
  long glGetColorTableParameterfv;
  long glGetColorTableParameteriv;
  long glColorSubTable;
  long glCopyColorSubTable;
  long glConvolutionFilter1D;
  long glConvolutionFilter2D;
  long glConvolutionParameterf;
  long glConvolutionParameterfv;
  long glConvolutionParameteri;
  long glConvolutionParameteriv;
  long glCopyConvolutionFilter1D;
  long glCopyConvolutionFilter2D;
  long glGetConvolutionFilter;
  long glGetConvolutionParameterfv;
  long glGetConvolutionParameteriv;
  long glGetSeparableFilter;
  long glSeparableFilter2D;
  long glGetHistogram;
  long glGetHistogramParameterfv;
  long glGetHistogramParameteriv;
  long glGetMinmax;
  long glGetMinmaxParameterfv;
  long glGetMinmaxParameteriv;
  long glHistogram;
  long glMinmax;
  long glResetHistogram;
  long glResetMinmax;
  long glTexImage3D;
  long glTexSubImage3D;
  long glCopyTexSubImage3D;

  // OpenGL 1.3
  long glActiveTexture;
  long glClientActiveTexture;
  long glMultiTexCoord1d;
  long glMultiTexCoord1dv;
  long glMultiTexCoord1f;
  long glMultiTexCoord1fv;
  long glMultiTexCoord1i;
  long glMultiTexCoord1iv;
  long glMultiTexCoord1s;
  long glMultiTexCoord1sv;
  long glMultiTexCoord2d;
  long glMultiTexCoord2dv;
  long glMultiTexCoord2f;
  long glMultiTexCoord2fv;
  long glMultiTexCoord2i;
  long glMultiTexCoord2iv;
  long glMultiTexCoord2s;
  long glMultiTexCoord2sv;
  long glMultiTexCoord3d;
  long glMultiTexCoord3dv;
  long glMultiTexCoord3f;
  long glMultiTexCoord3fv;
  long glMultiTexCoord3i;
  long glMultiTexCoord3iv;
  long glMultiTexCoord3s;
  long glMultiTexCoord3sv;
  long glMultiTexCoord4d;
  long glMultiTexCoord4dv;
  long glMultiTexCoord4f;
  long glMultiTexCoord4fv;
  long glMultiTexCoord4i;
  long glMultiTexCoord4iv;
  long glMultiTexCoord4s;
  long glMultiTexCoord4sv;
  long glLoadTransposeMatrixf;
  long glLoadTransposeMatrixd;
  long glMultTransposeMatrixf;
  long glMultTransposeMatrixd;
  long glSampleCoverage;
  long glCompressedTexImage3D;
  long glCompressedTexImage2D;
  long glCompressedTexImage1D;
  long glCompressedTexSubImage3D;
  long glCompressedTexSubImage2D;
  long glCompressedTexSubImage1D;
  long glGetCompressedTexImage;

  // OpenGL 1.4
  long glBlendFuncSeparate;

  // OpenGL 1.5
  long glGenQueries;

  private void getProcAddresses() {

    // OpenGL 1.2
    glBlendColor = getProcAddress("glBlendColor");
    glBlendEquation = getProcAddress("glBlendEquation");
    glDrawRangeElements = getProcAddress("glDrawRangeElements");
    glColorTable = getProcAddress("glColorTable");
    glColorTableParameterfv = getProcAddress("glColorTableParameterfv");
    glColorTableParameteriv = getProcAddress("glColorTableParameteriv");
    glCopyColorTable = getProcAddress("glCopyColorTable");
    glGetColorTable = getProcAddress("glGetColorTable");
    glGetColorTableParameterfv = getProcAddress("glGetColorTableParameterfv");
    glGetColorTableParameteriv = getProcAddress("glGetColorTableParameteriv");
    glColorSubTable = getProcAddress("glColorSubTable");
    glCopyColorSubTable = getProcAddress("glCopyColorSubTable");
    glConvolutionFilter1D = getProcAddress("glConvolutionFilter1D");
    glConvolutionFilter2D = getProcAddress("glConvolutionFilter2D");
    glConvolutionParameterf = getProcAddress("glConvolutionParameterf");
    glConvolutionParameterfv = getProcAddress("glConvolutionParameterfv");
    glConvolutionParameteri = getProcAddress("glConvolutionParameteri");
    glConvolutionParameteriv = getProcAddress("glConvolutionParameteriv");
    glCopyConvolutionFilter1D = getProcAddress("glCopyConvolutionFilter1D");
    glCopyConvolutionFilter2D = getProcAddress("glCopyConvolutionFilter2D");
    glGetConvolutionFilter = getProcAddress("glGetConvolutionFilter");
    glGetConvolutionParameterfv = getProcAddress("glGetConvolutionParameterfv");
    glGetConvolutionParameteriv = getProcAddress("glGetConvolutionParameteriv");
    glGetSeparableFilter = getProcAddress("glGetSeparableFilter");
    glSeparableFilter2D = getProcAddress("glSeparableFilter2D");
    glGetHistogram = getProcAddress("glGetHistogram");
    glGetHistogramParameterfv = getProcAddress("glGetHistogramParameterfv");
    glGetHistogramParameteriv = getProcAddress("glGetHistogramParameteriv");
    glGetMinmax = getProcAddress("glGetMinmax");
    glGetMinmaxParameterfv = getProcAddress("glGetMinmaxParameterfv");
    glGetMinmaxParameteriv = getProcAddress("glGetMinmaxParameteriv");
    glHistogram = getProcAddress("glHistogram");
    glMinmax = getProcAddress("glMinmax");
    glResetHistogram = getProcAddress("glResetHistogram");
    glResetMinmax = getProcAddress("glResetMinmax");
    glTexImage3D = getProcAddress("glTexImage3D");
    glTexSubImage3D = getProcAddress("glTexSubImage3D");
    glCopyTexSubImage3D = getProcAddress("glCopyTexSubImage3D");

    // OpenGL 1.3
    glActiveTexture = getProcAddress("glActiveTexture");
    glClientActiveTexture = getProcAddress("glClientActiveTexture");
    glMultiTexCoord1d = getProcAddress("glMultiTexCoord1d");
    glMultiTexCoord1dv = getProcAddress("glMultiTexCoord1dv");
    glMultiTexCoord1f = getProcAddress("glMultiTexCoord1f");
    glMultiTexCoord1fv = getProcAddress("glMultiTexCoord1fv");
    glMultiTexCoord1i = getProcAddress("glMultiTexCoord1i");
    glMultiTexCoord1iv = getProcAddress("glMultiTexCoord1iv");
    glMultiTexCoord1s = getProcAddress("glMultiTexCoord1s");
    glMultiTexCoord1sv = getProcAddress("glMultiTexCoord1sv");
    glMultiTexCoord2d = getProcAddress("glMultiTexCoord2d");
    glMultiTexCoord2dv = getProcAddress("glMultiTexCoord2dv");
    glMultiTexCoord2f = getProcAddress("glMultiTexCoord2f");
    glMultiTexCoord2fv = getProcAddress("glMultiTexCoord2fv");
    glMultiTexCoord2i = getProcAddress("glMultiTexCoord2i");
    glMultiTexCoord2iv = getProcAddress("glMultiTexCoord2iv");
    glMultiTexCoord2s = getProcAddress("glMultiTexCoord2s");
    glMultiTexCoord2sv = getProcAddress("glMultiTexCoord2sv");
    glMultiTexCoord3d = getProcAddress("glMultiTexCoord3d");
    glMultiTexCoord3dv = getProcAddress("glMultiTexCoord3dv");
    glMultiTexCoord3f = getProcAddress("glMultiTexCoord3f");
    glMultiTexCoord3fv = getProcAddress("glMultiTexCoord3fv");
    glMultiTexCoord3i = getProcAddress("glMultiTexCoord3i");
    glMultiTexCoord3iv = getProcAddress("glMultiTexCoord3iv");
    glMultiTexCoord3s = getProcAddress("glMultiTexCoord3s");
    glMultiTexCoord3sv = getProcAddress("glMultiTexCoord3sv");
    glMultiTexCoord4d = getProcAddress("glMultiTexCoord4d");
    glMultiTexCoord4dv = getProcAddress("glMultiTexCoord4dv");
    glMultiTexCoord4f = getProcAddress("glMultiTexCoord4f");
    glMultiTexCoord4fv = getProcAddress("glMultiTexCoord4fv");
    glMultiTexCoord4i = getProcAddress("glMultiTexCoord4i");
    glMultiTexCoord4iv = getProcAddress("glMultiTexCoord4iv");
    glMultiTexCoord4s = getProcAddress("glMultiTexCoord4s");
    glMultiTexCoord4sv = getProcAddress("glMultiTexCoord4sv");
    glLoadTransposeMatrixf = getProcAddress("glLoadTransposeMatrixf");
    glLoadTransposeMatrixd = getProcAddress("glLoadTransposeMatrixd");
    glMultTransposeMatrixf = getProcAddress("glMultTransposeMatrixf");
    glMultTransposeMatrixd = getProcAddress("glMultTransposeMatrixd");
    glSampleCoverage = getProcAddress("glSampleCoverage");
    glCompressedTexImage3D = getProcAddress("glCompressedTexImage3D");
    glCompressedTexImage2D = getProcAddress("glCompressedTexImage2D");
    glCompressedTexImage1D = getProcAddress("glCompressedTexImage1D");
    glCompressedTexSubImage3D = getProcAddress("glCompressedTexSubImage3D");
    glCompressedTexSubImage2D = getProcAddress("glCompressedTexSubImage2D");
    glCompressedTexSubImage1D = getProcAddress("glCompressedTexSubImage1D");
    glGetCompressedTexImage = getProcAddress("glGetCompressedTexImage");

    // OpenGL 1.4
    glBlendFuncSeparate = getProcAddress("glBlendFuncSeparate");

    // OpenGL 1.5
    glGenQueries = getProcAddress("glGenQueries");

    // Do this only once.
    _gotProcAddresses = true;
  }
  private static native long getProcAddress(String functionName);

  /**
   * Stripped-down version of Doug Lea's reentrant lock.
   * TODO: replace with JDK 5.0 standard lock.
   */
  private static class ReentrantLock {
    public void acquire() {
      Check.state(!Thread.interrupted(),"thread is not interrupted");
      Thread caller = Thread.currentThread();
      synchronized(this) {
        if (_owner==caller) 
          ++_holds;
        else {
          try {  
            while (_owner != null) 
              wait(); 
            _owner = caller;
            _holds = 1;
          }
          catch (InterruptedException ex) {
            notify();
            Check.state(false,"thread is not interrupted");
          }
        }
      }
    }
    public synchronized void release() {
      Check.state(_owner==Thread.currentThread(),"thread owns the lock");
      if (--_holds == 0) {
        _owner = null;
        notify(); 
      }
    }
    private Thread _owner = null;
    private long _holds = 0;
  }

  static {
    System.loadLibrary("edu_mines_jves_opengl");
  }
}