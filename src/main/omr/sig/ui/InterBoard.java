//------------------------------------------------------------------------------------------------//
//                                                                                                //
//                                       I n t e r B o a r d                                      //
//                                                                                                //
//------------------------------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">
//  Copyright © Herve Bitteur and others 2000-2016. All rights reserved.
//  This software is released under the GNU General Public License.
//  Goto http://kenai.com/projects/audiveris to report bugs or suggestions.
//------------------------------------------------------------------------------------------------//
// </editor-fold>
package omr.sig.ui;

import omr.constant.ConstantSet;

import omr.glyph.Shape;

import omr.ui.selection.EntityListEvent;
import omr.ui.selection.MouseMovement;
import omr.ui.selection.UserEvent;

import omr.sheet.Sheet;

import omr.sig.inter.Inter;

import omr.ui.Board;
import omr.ui.EntityBoard;
import omr.ui.PixelCount;
import omr.ui.field.LTextField;
import omr.ui.util.Panel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Class {@code InterBoard} defines a UI board for {@link Inter} information.
 *
 * @author Hervé Bitteur
 */
public class InterBoard
        extends EntityBoard<Inter>
{
    //~ Static fields/initializers -----------------------------------------------------------------

    private static final Constants constants = new Constants();

    private static final Logger logger = LoggerFactory.getLogger(InterBoard.class);

    //~ Instance fields ----------------------------------------------------------------------------
    /** Related sheet. */
    private final Sheet sheet;

    /** Output : shape icon. */
    private final JLabel shapeIcon = new JLabel();

    /** Output : grade (intrinsic/contextual). */
    private final LTextField grade = new LTextField("Grade", "Intrinsic / Contextual");

    /** Output : shape. */
    private final LTextField shapeField = new LTextField("", "Shape for this interpretation");

    /** Output : grade details. */
    private final JLabel details = new JLabel("");

    /** To delete/deassign. */
    private final DeassignAction deassignAction = new DeassignAction();

    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new InterBoard object.
     *
     * @param sheet the related sheet
     */
    public InterBoard (Sheet sheet)
    {
        super(Board.INTER, sheet.getInterIndex().getEntityService(), true); // initially expanded
        this.sheet = sheet;

        // Force a constant height for the shapeIcon field, despite variation in size of the icon
        Dimension dim = new Dimension(
                constants.shapeIconWidth.getValue(),
                constants.shapeIconHeight.getValue());
        shapeIcon.setPreferredSize(dim);
        shapeIcon.setMaximumSize(dim);
        shapeIcon.setMinimumSize(dim);

        details.setToolTipText("Grade details");
        details.setHorizontalAlignment(SwingConstants.CENTER);

        // Initial status
        grade.setEnabled(false);
        details.setEnabled(false);

        defineLayout();
    }

    //~ Methods ------------------------------------------------------------------------------------
    //---------//
    // onEvent //
    //---------//
    /**
     * Call-back triggered when Inter Selection has been modified
     *
     * @param event of current inter list
     */
    @Override
    public void onEvent (UserEvent event)
    {
        logger.debug("InterBoard event:{}", event);

        try {
            // Ignore RELEASING
            if (event.movement == MouseMovement.RELEASING) {
                return;
            }

            super.onEvent(event);

            if (event instanceof EntityListEvent) {
                handleEvent((EntityListEvent<Inter>) event);
            }
        } catch (Exception ex) {
            logger.warn(getClass().getName() + " onEvent error", ex);
        }
    }

    //---------------------//
    // dumpActionPerformed //
    //---------------------//
    @Override
    protected void dumpActionPerformed (ActionEvent e)
    {
        final Inter inter = getSelectedEntity();

        // Compute contextual grade
        if ((inter.getSig() != null) && !inter.isDeleted()) {
            inter.getSig().computeContextualGrade(inter, false);
        }

        super.dumpActionPerformed(e);
    }

    //---------------//
    // getFormLayout //
    //---------------//
    @Override
    protected FormLayout getFormLayout ()
    {
        return Panel.makeFormLayout(4, 3);
    }

    //--------------//
    // defineLayout //
    //--------------//
    /**
     * Define the layout for InterBoard specific fields.
     */
    private void defineLayout ()
    {
        final CellConstraints cst = new CellConstraints();

        // Layout
        int r = 1; // --------------------------------

        // Shape Icon (start, spans several rows) + layer + Deassign button
        builder.add(shapeIcon, cst.xywh(1, r, 1, 5));

        builder.add(grade.getLabel(), cst.xy(5, r));
        builder.add(grade.getField(), cst.xy(7, r));

        JButton deassignButton = new JButton(deassignAction);
        deassignButton.setHorizontalTextPosition(SwingConstants.LEFT);
        deassignButton.setHorizontalAlignment(SwingConstants.RIGHT);
        deassignAction.setEnabled(false);
        builder.add(deassignButton, cst.xyw(9, r, 3));

        r += 2; // --------------------------------

        builder.add(shapeField.getField(), cst.xyw(7, r, 5));

        r += 2; // --------------------------------

        builder.add(details, cst.xyw(1, r, 11));
    }

    //-------------//
    // handleEvent //
    //-------------//
    /**
     * Interest in InterList
     *
     * @param interListEvent
     */
    private void handleEvent (EntityListEvent<Inter> interListEvent)
    {
        final Inter inter = interListEvent.getEntity();

        // Shape text and icon
        Shape shape = (inter != null) ? inter.getShape() : null;

        if (shape != null) {
            shapeField.setText(shape.toString());
            shapeIcon.setIcon(shape.getDecoratedSymbol());
        } else {
            shapeField.setText((inter != null) ? inter.shapeString() : "");
            shapeIcon.setIcon(null);
        }

        // Inter characteristics
        if (inter != null) {
            vip.getLabel().setEnabled(true);
            vip.getField().setEnabled(!inter.isVip());
            vip.getField().setSelected(inter.isVip());

            Double cp = inter.getContextualGrade();

            if (cp != null) {
                grade.setText(String.format("%.2f/%.2f", inter.getGrade(), cp));
            } else {
                grade.setText(String.format("%.2f", inter.getGrade()));
            }

            details.setText((inter.getImpacts() == null) ? "" : inter.getImpacts().toString());
            deassignAction.putValue(Action.NAME, inter.isDeleted() ? "deleted" : "Deassign");
        } else {
            vip.setEnabled(false);
            vip.getField().setSelected(false);

            grade.setText("");
            details.setText("");
            deassignAction.putValue(Action.NAME, " ");
        }

        deassignAction.setEnabled((inter != null) && !inter.isDeleted());
        grade.setEnabled(inter != null);
        shapeField.setEnabled(inter != null);
        details.setEnabled(inter != null);
    }

    //~ Inner Classes ------------------------------------------------------------------------------
    //-----------//
    // Constants //
    //-----------//
    private static final class Constants
            extends ConstantSet
    {
        //~ Instance fields ------------------------------------------------------------------------

        private final PixelCount shapeIconHeight = new PixelCount(
                70,
                "Exact pixel height for the shape icon field");

        private final PixelCount shapeIconWidth = new PixelCount(
                50,
                "Exact pixel width for the shape icon field");
    }

    //----------------//
    // DeassignAction //
    //----------------//
    private static class DeassignAction
            extends AbstractAction
    {
        //~ Constructors ---------------------------------------------------------------------------

        public DeassignAction ()
        {
            super("Deassign");
            this.putValue(Action.SHORT_DESCRIPTION, "Deassign inter");
        }

        //~ Methods --------------------------------------------------------------------------------
        @Override
        public void actionPerformed (ActionEvent e)
        {
            logger.info("Not yet implemented");
        }
    }
}
