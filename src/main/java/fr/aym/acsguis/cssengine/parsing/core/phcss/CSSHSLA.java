/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.aym.acsguis.cssengine.parsing.core.phcss;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Objects;

/**
 * Represents a single HSLA color value (hue, saturation, lightness, opacity).
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class CSSHSLA {
    private String m_sHue;
    private String m_sSaturation;
    private String m_sLightness;
    private String m_sOpacity;

    /**
     * Copy constructor
     *
     * @param aOther
     *        The object to copy the data from. May not be <code>null</code>.
     */
    public CSSHSLA(@Nonnull final CSSHSLA aOther) {
        this(aOther.getHue(), aOther.getSaturation(), aOther.getLightness(), aOther.getOpacity());
    }

    /**
     * Constructor
     *
     * @param aOther
     *        The HSL value to use as the basis. May not be <code>null</code>.
     * @param fOpacity
     *        Opacity part. Is fitted to a value between 0 and 1.
     * @since 3.8.3
     */
    public CSSHSLA(@Nonnull final CSSHSL aOther, final float fOpacity) {
        this(aOther, Float.toString(CSSColorHelper.getOpacityToUse(fOpacity)));
    }

    /**
     * Constructor
     *
     * @param aOther
     *        The HSL value to use as the basis. May not be <code>null</code>.
     * @param sOpacity
     *        Opacity part. May neither be <code>null</code> nor empty.
     * @since 3.8.3
     */
    public CSSHSLA(@Nonnull final CSSHSL aOther, @Nonnull final String sOpacity) {
        this(aOther.getHue(), aOther.getSaturation(), aOther.getLightness(), sOpacity);
    }

    /**
     * Constructor
     *
     * @param nHue
     *        Hue value. Is scaled to the range 0-360
     * @param nSaturation
     *        Saturation value. Is cut to the range 0-100 (percentage)
     * @param nLightness
     *        Lightness value. Is cut to the range 0-100 (percentage)
     * @param fOpacity
     *        Opacity - is scaled to 0-1
     */
    public CSSHSLA(final int nHue, final int nSaturation, final int nLightness, final float fOpacity) {
        this(Integer.toString(CSSColorHelper.getHSLHueValue(nHue)),
                Integer.toString(CSSColorHelper.getHSLPercentageValue(nSaturation)) + "%",
                Integer.toString(CSSColorHelper.getHSLPercentageValue(nLightness)) + "%",
                Float.toString(CSSColorHelper.getOpacityToUse(fOpacity)));
    }

    /**
     * Constructor
     *
     * @param fHue
     *        Hue value. Is scaled to the range 0-360
     * @param fSaturation
     *        Saturation value. Is cut to the range 0-100 (percentage)
     * @param fLightness
     *        Lightness value. Is cut to the range 0-100 (percentage)
     * @param fOpacity
     *        Opacity - is scaled to 0-1
     */
    public CSSHSLA(final float fHue, final float fSaturation, final float fLightness, final float fOpacity) {
        this(Float.toString(CSSColorHelper.getHSLHueValue(fHue)),
                Float.toString(CSSColorHelper.getHSLPercentageValue(fSaturation)) + "%",
                Float.toString(CSSColorHelper.getHSLPercentageValue(fLightness)) + "%",
                Float.toString(CSSColorHelper.getOpacityToUse(fOpacity)));
    }

    public CSSHSLA(@Nonnull final String sHue,
                   @Nonnull final String sSaturation,
                   @Nonnull final String sLightness,
                   @Nonnull final String sOpacity) {
        setHue(sHue);
        setSaturation(sSaturation);
        setLightness(sLightness);
        setOpacity(sOpacity);
    }

    /**
     * @return hue part
     */
    @Nonnull
    public String getHue() {
        return m_sHue;
    }

    @Nonnull
    public CSSHSLA setHue(@Nonnull final String sHue) {
        m_sHue = sHue;
        return this;
    }

    /**
     * @return saturation part
     */
    @Nonnull
    public String getSaturation() {
        return m_sSaturation;
    }

    @Nonnull
    public CSSHSLA setSaturation(@Nonnull final String sSaturation) {
        m_sSaturation = sSaturation;
        return this;
    }

    /**
     * @return lightness part
     */
    @Nonnull
    public String getLightness() {
        return m_sLightness;
    }

    @Nonnull
    public CSSHSLA setLightness(@Nonnull final String sLightness) {
        m_sLightness = sLightness;
        return this;
    }

    /**
     * @return opacity part
     */
    @Nonnull
    public String getOpacity() {
        return m_sOpacity;
    }

    @Nonnull
    public CSSHSLA setOpacity(@Nonnull final String sOpacity) {
        m_sOpacity = sOpacity;
        return this;
    }

    /**
     * @return This value as HSL value without the opacity. Never
     *         <code>null</code>.
     * @since 3.8.3
     */
    @Nonnull
    public CSSHSL getAsHSL() {
        return new CSSHSL(m_sHue, m_sSaturation, m_sLightness);
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.8.3
     */
    @Nonnull
    public String getAsString() {
        return CCSSValue.PREFIX_HSLA_OPEN +
                m_sHue +
                ',' +
                m_sSaturation +
                ',' +
                m_sLightness +
                ',' +
                m_sOpacity +
                CCSSValue.SUFFIX_HSLA_CLOSE;
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.8.3
     */
    @Nonnull
    public CSSHSLA getClone() {
        return new CSSHSLA(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (o == null || !getClass().equals(o.getClass()))
            return false;
        final CSSHSLA rhs = (CSSHSLA) o;
        return m_sHue.equals(rhs.m_sHue) &&
                m_sSaturation.equals(rhs.m_sSaturation) &&
                m_sLightness.equals(rhs.m_sLightness) &&
                m_sOpacity.equals(rhs.m_sOpacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_sHue, m_sSaturation, m_sLightness, m_sOpacity);
    }

    @Override
    public String toString() {
        return "CSSHSLA{" +
                "m_sHue='" + m_sHue + '\'' +
                ", m_sSaturation='" + m_sSaturation + '\'' +
                ", m_sLightness='" + m_sLightness + '\'' +
                ", m_sOpacity='" + m_sOpacity + '\'' +
                '}';
    }
}
