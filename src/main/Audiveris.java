import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

//------------------------------------------------------------------------------------------------//
//                                                                                                //
//                                       A u d i v e r i s                                        //
//                                                                                                //
//------------------------------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">
//
//  Copyright © Audiveris 2018. All rights reserved.
//
//  This program is free software: you can redistribute it and/or modify it under the terms of the
//  GNU Affero General Public License as published by the Free Software Foundation, either version
//  3 of the License, or (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
//  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//  See the GNU Affero General Public License for more details.
//
//  You should have received a copy of the GNU Affero General Public License along with this
//  program.  If not, see <http://www.gnu.org/licenses/>.
//------------------------------------------------------------------------------------------------//
// </editor-fold>

/**
 * Class {@code Audiveris} is simply a convenient entry point to OMR, which
 * delegates the call to {@link org.audiveris.omr.Main#main}.
 *
 * @author Hervé Bitteur
 */
public final class Audiveris {
    // ~ Constructors
    // -------------------------------------------------------------------------------

    /** To avoid instantiation. */
    private Audiveris() {
    }

    // ~ Methods
    // ------------------------------------------------------------------------------------
    // ------//
    // main //
    // ------//
    /**
     * The main entry point, which just calls {@link org.audiveris.omr.Main#main}.
     *
     * @param args These arguments are simply passed to Main
     */
    public static void main(final String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "Audiveris");

        if (System.getProperty("os.name", "").startsWith("Mac OS")) {
            try {
                // UIManager.setLookAndFeel("org.violetlib.aqua.AquaLookAndFeel");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                    | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        }

        org.audiveris.omr.Main.main(args);
    }
}
