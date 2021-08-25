/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.aym.acsguis.cssengine.parsing.core.phcss;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.RegEx;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides color handling sanity methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class CSSColorHelper
{
  /** RGB minimum value */
  public static final int RGB_MIN = 0;
  /** RGB maximum value */
  public static final int RGB_MAX = 255;
  /** RGB range (max-min+1) */
  public static final int RGB_RANGE = 256;
  /** HSL minimum value (for Hue only) */
  public static final int HSL_MIN = 0;
  /** HSL maximum value (for Hue only) */
  public static final int HSL_MAX = 359;
  /** HSL range (max-min+1) (for Hue only) */
  public static final int HSL_RANGE = 360;
  /** Percentage minimum value (for HSL) */
  public static final int PERCENTAGE_MIN = 0;
  /** Percentage maximum value (for HSL) */
  public static final int PERCENTAGE_MAX = 100;
  /** Minimum opacity value */
  public static final float OPACITY_MIN = 0f;
  /** Maximum opacity value */
  public static final float OPACITY_MAX = 1f;

  @RegEx
  private static final String PATTERN_PART_VALUE = "((?:\\+|\\-)?(?:[0-9]*\\.[0-9]*|[0-9]+)%?)";
  @RegEx
  private static final String PATTERN_PART_PERCENTAGE = "((?:\\+|\\-)?(?:(?:[0-9]*\\.[0-9]*|[0-9]+)%|0))";
  @RegEx
  private static final String PATTERN_PART_OPACITY = "([0-9]*\\.[0-9]*|[0-9]+)";
  @RegEx
  private static final String PATTERN_RGB = "^" +
                                            CCSSValue.PREFIX_RGB +
                                            "\\s*\\(\\s*" +
                                            PATTERN_PART_VALUE +
                                            "\\s*,\\s*" +
                                            PATTERN_PART_VALUE +
                                            "\\s*,\\s*" +
                                            PATTERN_PART_VALUE +
                                            "\\s*\\)$";
  @RegEx
  private static final String PATTERN_RGBA = "^" +
                                             CCSSValue.PREFIX_RGBA +
                                             "\\s*\\(\\s*" +
                                             PATTERN_PART_VALUE +
                                             "\\s*,\\s*" +
                                             PATTERN_PART_VALUE +
                                             "\\s*,\\s*" +
                                             PATTERN_PART_VALUE +
                                             "\\s*,\\s*" +
                                             PATTERN_PART_OPACITY +
                                             "\\s*\\)$";
  @RegEx
  private static final String PATTERN_HSL = "^" +
                                            CCSSValue.PREFIX_HSL +
                                            "\\s*\\(\\s*" +
                                            PATTERN_PART_VALUE +
                                            "\\s*,\\s*" +
                                            PATTERN_PART_PERCENTAGE +
                                            "\\s*,\\s*" +
                                            PATTERN_PART_PERCENTAGE +
                                            "\\s*\\)$";
  @RegEx
  private static final String PATTERN_HSLA = "^" +
                                             CCSSValue.PREFIX_HSLA +
                                             "\\s*\\(\\s*" +
                                             PATTERN_PART_VALUE +
                                             "\\s*,\\s*" +
                                             PATTERN_PART_PERCENTAGE +
                                             "\\s*,\\s*" +
                                             PATTERN_PART_PERCENTAGE +
                                             "\\s*,\\s*" +
                                             PATTERN_PART_OPACITY +
                                             "\\s*\\)$";
  @RegEx
  private static final String PATTERN_HEX = "^" + CCSSValue.PREFIX_HEX + "([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$";

  private static final CSSColorHelper s_aInstance = new CSSColorHelper ();

  private CSSColorHelper()
  {}

  /**
   * Check if the passed string is any color value.
   *
   * @param sValue
   *        The value to check. May be <code>null</code>.
   * @return <code>true</code> if the passed value is not <code>null</code>, not
   *         empty and a valid CSS color value.
   * @see #isRGBColorValue(String)
   * @see #isRGBAColorValue(String)
   * @see #isHSLColorValue(String)
   * @see #isHSLAColorValue(String)
   * @see #isHexColorValue(String)
   * @see ECSSColor#isDefaultColorName(String)
   * @see ECSSColorName#isDefaultColorName(String)
   * @see CCSSValue#CURRENTCOLOR
   * @see CCSSValue#TRANSPARENT
   */
  public static boolean isColorValue (@Nullable final String sValue)
  {
    if(sValue == null)
      return false;
    final String sRealValue = sValue.trim();
    if (sRealValue.isEmpty())
      return false;

    return isRGBColorValue (sRealValue) ||
           isRGBAColorValue (sRealValue) ||
           isHSLColorValue (sRealValue) ||
           isHSLAColorValue (sRealValue) ||
           isHexColorValue (sRealValue) ||
           CssColors.isDefaultColorName (sRealValue) ||
            CssColors.isDefaultColorName (sRealValue) ||
           sRealValue.equals (CCSSValue.CURRENTCOLOR) ||
           sRealValue.equals (CCSSValue.TRANSPARENT);
  }

  /**
   * Check if the passed String is valid CSS RGB color value. Example value:
   * <code>rgb(255,0,0)</code>
   *
   * @param sValue
   *        The value to check. May be <code>null</code>.
   * @return <code>true</code> if it is a CSS RGB color value,
   *         <code>false</code> if not
   */
  public static boolean isRGBColorValue (@Nullable final String sValue)
  {
    if(sValue == null)
      return false;
    final String sRealValue = sValue.trim();
    // startsWith as speedup
    return sRealValue.startsWith(CCSSValue.PREFIX_RGB) &&
            Pattern.compile(PATTERN_RGB).matcher(sRealValue).matches(); //TODO CACHE IT
  }

  /**
   * Extract the CSS RGB color value from the passed String. Example value:
   * <code>rgb(255,0,0)</code>
   *
   * @param sValue
   *        The value to extract the value from. May be <code>null</code>.
   * @return <code>null</code> if the passed value is not a valid CSS RGB color
   *         value.
   */
  @Nullable
  public static CSSRGB getParsedRGBColorValue (@Nullable final String sValue)
  {
    if(sValue == null)
      return null;
    final String sRealValue = sValue.trim();
    if (sRealValue.startsWith (CCSSValue.PREFIX_RGB))
    {
      final String [] aValues = getAllMatchingGroupValues (PATTERN_RGB, sRealValue);
      if (aValues != null)
        return new CSSRGB (aValues[0], aValues[1], aValues[2]);
    }
    return null;
  }

  //RegExHelper

  @Nullable
  public static String [] getAllMatchingGroupValues (@Nonnull @RegEx final String sRegEx, @Nonnull final String sValue)
  {
    final Matcher aMatcher = Pattern.compile(sRegEx).matcher(sValue);
    if (!aMatcher.find ())
    {
      // Values does not match RegEx
      return null;
    }

    // groupCount is excluding the .group(0) match!!!
    final int nGroupCount = aMatcher.groupCount ();
    final String [] ret = new String [nGroupCount];
    for (int i = 0; i < nGroupCount; ++i)
      ret[i] = aMatcher.group (i + 1);
    return ret;
  }

  //End

  /**
   * Check if the passed String is valid CSS RGBA color value. Example value:
   * <code>rgba(255,0,0, 0.1)</code>
   *
   * @param sValue
   *        The value to check. May be <code>null</code>.
   * @return <code>true</code> if it is a CSS RGBA color value,
   *         <code>false</code> if not
   */
  public static boolean isRGBAColorValue (@Nullable final String sValue)
  {
    if(sValue == null)
      return false;
    final String sRealValue = sValue.trim();
    return sRealValue.startsWith (CCSSValue.PREFIX_RGBA) &&
           Pattern.compile(PATTERN_RGBA).matcher(sRealValue).matches();
  }

  @Nullable
  public static CSSRGBA getParsedRGBAColorValue (@Nullable final String sValue)
  {
    if(sValue == null)
      return null;
    final String sRealValue = sValue.trim();
    if (sRealValue.startsWith (CCSSValue.PREFIX_RGBA))
    {
      final String [] aValues = getAllMatchingGroupValues (PATTERN_RGBA, sRealValue);
      if (aValues != null)
        return new CSSRGBA (aValues[0], aValues[1], aValues[2], aValues[3]);
    }
    return null;
  }

  /**
   * Check if the passed String is valid CSS HSL color value. Example value:
   * <code>hsl(255,0%,0%)</code>
   *
   * @param sValue
   *        The value to check. May be <code>null</code>.
   * @return <code>true</code> if it is a CSS HSL color value,
   *         <code>false</code> if not
   */
  public static boolean isHSLColorValue (@Nullable final String sValue)
  {
    if(sValue == null)
      return false;
    final String sRealValue = sValue.trim();
    return sRealValue.startsWith (CCSSValue.PREFIX_HSL) &&
            Pattern.compile(PATTERN_HSL).matcher(sRealValue).matches();
  }

  @Nullable
  public static CSSHSL getParsedHSLColorValue (@Nullable final String sValue)
  {
    if(sValue == null)
      return null;
    final String sRealValue = sValue.trim();
    if (sRealValue.startsWith (CCSSValue.PREFIX_HSL))
    {
      final String [] aValues = getAllMatchingGroupValues (PATTERN_HSL, sRealValue);
      if (aValues != null)
        return new CSSHSL (aValues[0], aValues[1], aValues[2]);
    }
    return null;
  }

  /**
   * Check if the passed String is valid CSS HSLA color value. Example value:
   * <code>hsla(255,0%,0%, 0.1)</code>
   *
   * @param sValue
   *        The value to check. May be <code>null</code>.
   * @return <code>true</code> if it is a CSS HSLA color value,
   *         <code>false</code> if not
   */
  public static boolean isHSLAColorValue (@Nullable final String sValue)
  {
    if(sValue == null)
      return false;
    final String sRealValue = sValue.trim();
    return sRealValue.startsWith (CCSSValue.PREFIX_HSLA) &&
            Pattern.compile(PATTERN_HSLA).matcher(sRealValue).matches();
  }

  @Nullable
  public static CSSHSLA getParsedHSLAColorValue (@Nullable final String sValue)
  {
    if(sValue == null)
      return null;
    final String sRealValue = sValue.trim();
    if (sRealValue.startsWith (CCSSValue.PREFIX_HSLA))
    {
      final String [] aValues = getAllMatchingGroupValues (PATTERN_HSLA, sRealValue);
      if (aValues != null)
        return new CSSHSLA (aValues[0], aValues[1], aValues[2], aValues[3]);
    }
    return null;
  }

  /**
   * Check if the passed String is valid CSS hex color value. Example value:
   * <code>#ff0000</code>
   *
   * @param sValue
   *        The value to check. May be <code>null</code>.
   * @return <code>true</code> if it is a CSS hex color value,
   *         <code>false</code> if not
   */
  public static boolean isHexColorValue (@Nullable final String sValue)
  {
    if(sValue == null)
      return false;
    final String sRealValue = sValue.trim();
    return sRealValue.charAt (0) == CCSSValue.PREFIX_HEX &&
            Pattern.compile(PATTERN_HEX).matcher(sRealValue).matches();
  }

  @Nonnegative
  private static int _mod (final int nValue, final int nMod)
  {
    // modulo does not work as expected on negative numbers
    int nPositiveValue = nValue;
    while (nPositiveValue < 0)
      nPositiveValue += nMod;
    return nPositiveValue % nMod;
  }

  @Nonnegative
  private static float _mod (final float nValue, final int nMod)
  {
    // modulo does not work as expected on negative numbers
    float nPositiveValue = nValue;
    while (nPositiveValue < 0)
      nPositiveValue += nMod;
    return nPositiveValue % nMod;
  }

  /**
   * Convert the passed value to a valid RGB value in the range 0-255.
   *
   * @param nRGBPart
   *        The original value
   * @return The value between 0 and 255.
   */
  @Nonnegative
  public static int getRGBValue (final int nRGBPart)
  {
    return _mod (nRGBPart, RGB_RANGE);
  }

  /**
   * Ensure that the passed opacity value is in the range {@link #OPACITY_MIN}
   * and {@link #OPACITY_MAX}.
   *
   * @param fOpacity
   *        The original opacity
   * @return The opacity in the correct range between 0 and 1.
   */
  @Nonnegative
  public static float getOpacityToUse (final float fOpacity)
  {
    return fOpacity < OPACITY_MIN ? OPACITY_MIN : fOpacity > OPACITY_MAX ? OPACITY_MAX : fOpacity;
  }

  /**
   * Get the passed values as CSS RGB color value
   *
   * @param nRed
   *        Red - is scaled to 0-255
   * @param nGreen
   *        Green - is scaled to 0-255
   * @param nBlue
   *        Blue - is scaled to 0-255
   * @return The CSS string to use
   */
  @Nonnull
  public static String getRGBColorValue (final int nRed, final int nGreen, final int nBlue)
  {
    return new StringBuilder (16).append (CCSSValue.PREFIX_RGB_OPEN)
                                 .append (getRGBValue (nRed))
                                 .append (',')
                                 .append (getRGBValue (nGreen))
                                 .append (',')
                                 .append (getRGBValue (nBlue))
                                 .append (CCSSValue.SUFFIX_RGB_CLOSE)
                                 .toString ();
  }

  /**
   * Get the passed values as CSS RGBA color value
   *
   * @param nRed
   *        Red - is scaled to 0-255
   * @param nGreen
   *        Green - is scaled to 0-255
   * @param nBlue
   *        Blue - is scaled to 0-255
   * @param fOpacity
   *        Opacity to use - is scaled to 0-1.
   * @return The CSS string to use
   */
  @Nonnull
  public static String getRGBAColorValue (final int nRed, final int nGreen, final int nBlue, final float fOpacity)
  {
    return new StringBuilder (24).append (CCSSValue.PREFIX_RGBA_OPEN)
                                 .append (getRGBValue (nRed))
                                 .append (',')
                                 .append (getRGBValue (nGreen))
                                 .append (',')
                                 .append (getRGBValue (nBlue))
                                 .append (',')
                                 .append (getOpacityToUse (fOpacity))
                                 .append (CCSSValue.SUFFIX_RGBA_CLOSE)
                                 .toString ();
  }

  /**
   * Get the passed value as a valid HSL Hue value in the range of
   * {@value #HSL_MIN}-{@value #HSL_MAX}
   *
   * @param nHSLPart
   *        Source Hue
   * @return Hue value in the range of {@value #HSL_MIN}-{@value #HSL_MAX}
   */
  @Nonnegative
  public static int getHSLHueValue (final int nHSLPart)
  {
    return _mod (nHSLPart, HSL_RANGE);
  }

  /**
   * Get the passed value as a valid HSL Hue value in the range of
   * {@value #HSL_MIN}-{@value #HSL_MAX}
   *
   * @param fHSLPart
   *        Source Hue
   * @return Hue value in the range of {@value #HSL_MIN}-{@value #HSL_MAX}
   */
  @Nonnegative
  public static float getHSLHueValue (final float fHSLPart)
  {
    return _mod (fHSLPart, HSL_RANGE);
  }

  /**
   * Get the passed value as a valid HSL Saturation or Lightness value in the
   * range of {@value #PERCENTAGE_MIN}-{@value #PERCENTAGE_MAX} (percentage).
   *
   * @param nHSLPart
   *        Source value
   * @return Target value in the range of {@value #PERCENTAGE_MIN}-
   *         {@value #PERCENTAGE_MAX}
   */
  @Nonnegative
  public static int getHSLPercentageValue (final int nHSLPart)
  {
    return nHSLPart < PERCENTAGE_MIN ? PERCENTAGE_MIN : nHSLPart > PERCENTAGE_MAX ? PERCENTAGE_MAX : nHSLPart;
  }

  /**
   * Get the passed value as a valid HSL Saturation or Lightness value in the
   * range of {@link #PERCENTAGE_MIN}-{@link #PERCENTAGE_MAX} (percentage).
   *
   * @param nHSLPart
   *        Source value
   * @return Target value in the range of {@value #PERCENTAGE_MIN}-
   *         {@value #PERCENTAGE_MAX}
   */
  @Nonnegative
  public static float getHSLPercentageValue (final float nHSLPart)
  {
    return nHSLPart < PERCENTAGE_MIN ? PERCENTAGE_MIN : nHSLPart > PERCENTAGE_MAX ? PERCENTAGE_MAX : nHSLPart;
  }

  /**
   * Get the passed values as CSS HSL color value
   *
   * @param nHue
   *        Hue - is scaled to 0-359
   * @param nSaturation
   *        Saturation - is scaled to 0-100
   * @param nLightness
   *        Lightness - is scaled to 0-100
   * @return The CSS string to use
   */
  @Nonnull
  public static String getHSLColorValue (final int nHue, final int nSaturation, final int nLightness)
  {
    return new StringBuilder (18).append (CCSSValue.PREFIX_HSL_OPEN)
                                 .append (getHSLHueValue (nHue))
                                 .append (',')
                                 .append (getHSLPercentageValue (nSaturation))
                                 .append ("%,")
                                 .append (getHSLPercentageValue (nLightness))
                                 .append ("%")
                                 .append (CCSSValue.SUFFIX_HSL_CLOSE)
                                 .toString ();
  }

  /**
   * Get the passed values as CSS HSL color value
   *
   * @param fHue
   *        Hue - is scaled to 0-359
   * @param fSaturation
   *        Saturation - is scaled to 0-100
   * @param fLightness
   *        Lightness - is scaled to 0-100
   * @return The CSS string to use
   */
  @Nonnull
  public static String getHSLColorValue (final float fHue, final float fSaturation, final float fLightness)
  {
    return new StringBuilder (18).append (CCSSValue.PREFIX_HSL_OPEN)
                                 .append (getHSLHueValue (fHue))
                                 .append (',')
                                 .append (getHSLPercentageValue (fSaturation))
                                 .append ("%,")
                                 .append (getHSLPercentageValue (fLightness))
                                 .append ("%")
                                 .append (CCSSValue.SUFFIX_HSL_CLOSE)
                                 .toString ();
  }

  /**
   * Get the passed values as CSS HSLA color value
   *
   * @param nHue
   *        Hue - is scaled to 0-359
   * @param nSaturation
   *        Saturation - is scaled to 0-100
   * @param nLightness
   *        Lightness - is scaled to 0-100
   * @param fOpacity
   *        Opacity - is scaled to 0-1
   * @return The CSS string to use
   */
  @Nonnull
  public static String getHSLAColorValue (final int nHue,
                                          final int nSaturation,
                                          final int nLightness,
                                          final float fOpacity)
  {
    return new StringBuilder (32).append (CCSSValue.PREFIX_HSLA_OPEN)
                                 .append (getHSLHueValue (nHue))
                                 .append (',')
                                 .append (getHSLPercentageValue (nSaturation))
                                 .append ("%,")
                                 .append (getHSLPercentageValue (nLightness))
                                 .append ("%,")
                                 .append (getOpacityToUse (fOpacity))
                                 .append (CCSSValue.SUFFIX_HSLA_CLOSE)
                                 .toString ();
  }

  /**
   * Get the passed values as CSS HSLA color value
   *
   * @param fHue
   *        Hue - is scaled to 0-359
   * @param fSaturation
   *        Saturation - is scaled to 0-100
   * @param fLightness
   *        Lightness - is scaled to 0-100
   * @param fOpacity
   *        Opacity - is scaled to 0-1
   * @return The CSS string to use
   */
  @Nonnull
  public static String getHSLAColorValue (final float fHue,
                                          final float fSaturation,
                                          final float fLightness,
                                          final float fOpacity)
  {
    return new StringBuilder (32).append (CCSSValue.PREFIX_HSLA_OPEN)
                                 .append (getHSLHueValue (fHue))
                                 .append (',')
                                 .append (getHSLPercentageValue (fSaturation))
                                 .append ("%,")
                                 .append (getHSLPercentageValue (fLightness))
                                 .append ("%,")
                                 .append (getOpacityToUse (fOpacity))
                                 .append (CCSSValue.SUFFIX_HSLA_CLOSE)
                                 .toString ();
  }

  @Nonnull
  private static String _getRGBPartAsHexString (final int nRGBPart)
  {
    return getHexStringLeadingZero (getRGBValue (nRGBPart), 2);
  }

  //StringHelper :

  @Nonnull
  public static String getHexStringLeadingZero (final int nValue, final int nDigits)
  {
    if (nValue < 0)
      return "-" + getLeadingZero (getHexString (-nValue), nDigits - 1);
    return getLeadingZero (getHexString (nValue), nDigits);
  }

  @Nonnull
  public static String getHexString (final long nValue)
  {
    return Long.toString (nValue, 16);
  }

  @Nonnull
  public static String getLeadingZero (@Nonnull final String sValue, final int nChars)
  {
    return getWithLeading (sValue, nChars, '0');
  }

  @Nonnull
  public static String getWithLeading (@Nullable final String sSrc, @Nonnegative final int nMinLen, final char cFront)
  {
    return _getWithLeadingOrTrailing (sSrc, nMinLen, cFront, true);
  }

  @Nonnull
  private static String _getWithLeadingOrTrailing (@Nullable final String sSrc,
                                                   @Nonnegative final int nMinLen,
                                                   final char cGap,
                                                   final boolean bLeading)
  {
    if (nMinLen <= 0)
    {
      // Requested length is too short - return as is
      return getNotNull (sSrc, "");
    }

    final int nSrcLen = getLength (sSrc);
    if (nSrcLen == 0)
    {
      // Input string is empty
      return getRepeated (cGap, nMinLen);
    }

    final int nCharsToAdd = nMinLen - nSrcLen;
    if (nCharsToAdd <= 0)
    {
      // Input string is already longer than requested minimum length
      return sSrc;
    }

    final StringBuilder aSB = new StringBuilder (nMinLen);
    if (!bLeading)
      aSB.append (sSrc);
    for (int i = 0; i < nCharsToAdd; ++i)
      aSB.append (cGap);
    if (bLeading)
      aSB.append (sSrc);
    return aSB.toString ();
  }

  @Nullable
  public static String getNotNull (@Nullable final String s, final String sDefaultIfNull)
  {
    return s == null ? sDefaultIfNull : s;
  }

  @Nonnegative
  public static int getLength (@Nullable final CharSequence aCS)
  {
    return aCS == null ? 0 : aCS.length ();
  }

  @Nonnull
  public static String getRepeated (final char cElement, @Nonnegative final int nRepeats)
  {
    if (nRepeats == 0)
      return "";
    if (nRepeats == 1)
      return Character.toString (cElement);

    final char [] aElement = new char [nRepeats];
    Arrays.fill (aElement, cElement);
    return new String (aElement);
  }

  //End

  @Nonnull
  public static String getHexColorValue (final int nRed, final int nGreen, final int nBlue)
  {
    return new StringBuilder (CCSSValue.HEXVALUE_LENGTH).append (CCSSValue.PREFIX_HEX)
                                                        .append (_getRGBPartAsHexString (nRed))
                                                        .append (_getRGBPartAsHexString (nGreen))
                                                        .append (_getRGBPartAsHexString (nBlue))
                                                        .toString ();
  }

  /**
   * Get the passed RGB values as HSL values compliant for CSS in the CSS range
   * (0-359, 0-100, 0-100)
   *
   * @param nRed
   *        red value
   * @param nGreen
   *        green value
   * @param nBlue
   *        blue value
   * @return An array of 3 floats, containing hue, saturation and lightness (in
   *         this order). The first value is in the range 0-359, and the
   *         remaining two values are in the range 0-100 (percentage).
   */
  @Nonnull
  public static float [] getRGBAsHSLValue (final int nRed, final int nGreen, final int nBlue)
  {
    // Convert RGB to HSB(=HSL) - brightness vs. lightness
    int cmax = nRed > nGreen ? nRed : nGreen;
    if (nBlue > cmax)
      cmax = nBlue;
    int cmin = nRed < nGreen ? nRed : nGreen;
    if (nBlue < cmin)
      cmin = nBlue;

    final float brightness = cmax / 255.0f;
    float saturation;
    if (cmax != 0)
      saturation = ((float) (cmax - cmin)) / ((float) cmax);
    else
      saturation = 0;
    float hue;
    if (saturation == 0)
      hue = 0;
    else
    {
      final float redc = ((float) (cmax - nRed)) / ((float) (cmax - cmin));
      final float greenc = ((float) (cmax - nGreen)) / ((float) (cmax - cmin));
      final float bluec = ((float) (cmax - nBlue)) / ((float) (cmax - cmin));
      if (nRed == cmax)
        hue = bluec - greenc;
      else
        if (nGreen == cmax)
          hue = 2.0f + redc - bluec;
        else
          hue = 4.0f + greenc - redc;
      hue = hue / 6.0f;
      if (hue < 0)
        hue = hue + 1.0f;
    }
    return new float [] { hue * HSL_MAX, saturation * PERCENTAGE_MAX, brightness * PERCENTAGE_MAX };
  }

  /**
   * Get the passed RGB values as HSL values compliant for CSS in the CSS range
   * (0-359, 0-100, 0-100)
   *
   * @param fHue
   *        the hue component of the color - in the range 0-359
   * @param fSaturation
   *        the saturation of the color - in the range 0-100
   * @param fLightness
   *        the lightness of the color - in the range 0-100
   * @return An array of 3 ints, containing red, green and blue (in this order).
   *         All values are in the range 0-255.
   */
  @Nonnull
  public static int [] getHSLAsRGBValue (final float fHue, final float fSaturation, final float fLightness)
  {
    // Convert RGB to HSB(=HSL) - brightness vs. lightness
    final float hue = fHue / HSL_MAX;
    final float saturation = fSaturation / PERCENTAGE_MAX;
    final float brightness = fLightness / PERCENTAGE_MAX;
    int r = 0;
    int g = 0;
    int b = 0;
    if (saturation == 0)
    {
      r = g = b = (int) (brightness * 255.0f + 0.5f);
    }
    else
    {
      final float h = (hue - (float) Math.floor (hue)) * 6.0f;
      final float f = h - (float) Math.floor (h);
      final float p = brightness * (1.0f - saturation);
      final float q = brightness * (1.0f - saturation * f);
      final float t = brightness * (1.0f - (saturation * (1.0f - f)));
      switch ((int) h)
      {
        case 0:
          r = (int) (brightness * 255.0f + 0.5f);
          g = (int) (t * 255.0f + 0.5f);
          b = (int) (p * 255.0f + 0.5f);
          break;
        case 1:
          r = (int) (q * 255.0f + 0.5f);
          g = (int) (brightness * 255.0f + 0.5f);
          b = (int) (p * 255.0f + 0.5f);
          break;
        case 2:
          r = (int) (p * 255.0f + 0.5f);
          g = (int) (brightness * 255.0f + 0.5f);
          b = (int) (t * 255.0f + 0.5f);
          break;
        case 3:
          r = (int) (p * 255.0f + 0.5f);
          g = (int) (q * 255.0f + 0.5f);
          b = (int) (brightness * 255.0f + 0.5f);
          break;
        case 4:
          r = (int) (t * 255.0f + 0.5f);
          g = (int) (p * 255.0f + 0.5f);
          b = (int) (brightness * 255.0f + 0.5f);
          break;
        case 5:
          r = (int) (brightness * 255.0f + 0.5f);
          g = (int) (p * 255.0f + 0.5f);
          b = (int) (q * 255.0f + 0.5f);
          break;
      }
    }
    return new int [] { r, g, b };
  }
}
