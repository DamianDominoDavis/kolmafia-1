/**
 * Copyright (c) 2005, KoLmafia development team
 * http://kolmafia.sourceforge.net/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  [1] Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *  [2] Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in
 *      the documentation and/or other materials provided with the
 *      distribution.
 *  [3] Neither the name "KoLmafia development team" nor the names of
 *      its contributors may be used to endorse or promote products
 *      derived from this software without specific prior written
 *      permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.kolmafia;

// layout
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;

// event listeners
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// containers
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JCheckBox;
import javax.swing.text.JTextComponent;

// other imports
import net.java.dev.spellcast.utilities.SortedListModel;
import net.java.dev.spellcast.utilities.LockableListModel;
import net.java.dev.spellcast.utilities.JComponentUtilities;

/**
 * An extended <code>KoLFrame</code> which presents the user with the ability to
 * login to the Kingdom of Loathing.  Essentially, this class is a modification
 * of the <code>LoginDialog</code> class from the Spellcast project.
 */

public class LoginFrame extends KoLFrame
{
	private SortedListModel saveStateNames;

	/**
	 * Constructs a new <code>LoginFrame</code> which allows the user to
	 * log into the Kingdom of Loathing.  The <code>LoginFrame</code>
	 * assigns its <code>LoginPanel</code> as the content panel used by
	 * <code>KoLPanel</code> and other classes for updating its display,
	 * and derived classes may access the <code>LoginPanel</code> indirectly
	 * in this fashion.
	 *
	 * @param	client	The client associated with this <code>LoginFrame</code>.
	 */

	public LoginFrame( KoLmafia client, SortedListModel saveStateNames )
	{
		super( client, "Login" );
		setResizable( false );

		this.client = client;
		this.saveStateNames = new SortedListModel();
		this.saveStateNames.addAll( saveStateNames );
		contentPanel = new LoginPanel();

		JPanel imagePanel = new JPanel();
		imagePanel.setLayout( new BorderLayout( 0, 0 ) );
		imagePanel.add( new JLabel( " " ), BorderLayout.NORTH );
		imagePanel.add( new JLabel( JComponentUtilities.getSharedImage( "penguin.gif" ), JLabel.CENTER ), BorderLayout.SOUTH );

		getContentPane().add( imagePanel, BorderLayout.NORTH );
		getContentPane().add( contentPanel, BorderLayout.CENTER );
		addWindowListener( new ExitRequestAdapter() );
		addMenuBar();
	}

	/**
	 * Updates the display to reflect the given display state and
	 * to contain the given message.  Note that if there is no
	 * content panel, this method does nothing.
	 */

	public void updateDisplay( int displayState, String message )
	{
		if ( client != null )
			client.getLogStream().println( message );

		if ( contentPanel != null )
			contentPanel.setStatusMessage( displayState, message );

		switch ( displayState )
		{
			case DISABLED_STATE:
				setEnabled( false );
				break;

			default:
				setEnabled( true );
				break;
		}
	}

	/**
	 * Utility method used to add a menu bar to the <code>LoginFrame</code>.
	 * The menu bar contains configuration options and the general license
	 * information associated with <code>KoLmafia</code>.
	 */

	private void addMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar( menuBar );

		addStatusMenu( menuBar );
		addScriptMenu( menuBar );
		addOptionsMenu( menuBar );
		addHelpMenu( menuBar );
	}

	/**
	 * An internal class which represents the panel which is nested
	 * inside of the <code>LoginFrame</code>.
	 */

	private class LoginPanel extends KoLPanel implements ActionListener
	{
		private JPanel actionStatusPanel;
		private JLabel actionStatusLabel;

		private JComponent loginnameField;
		private JPasswordField passwordField;
		private JCheckBox getBreakfastCheckBox;
		private JCheckBox savePasswordCheckBox;
		private JCheckBox autoLoginCheckBox;

		/**
		 * Constructs a new <code>LoginPanel</code>, containing a place
		 * for the users to input their login name and password.  This
		 * panel, because it is intended to be the content panel for
		 * status message updates, also has a status label.
		 */

		public LoginPanel()
		{
			super( "login", "qlogin", "cancel" );

			actionStatusPanel = new JPanel();
			actionStatusPanel.setLayout( new GridLayout( 2, 1 ) );

			actionStatusLabel = new JLabel( " ", JLabel.CENTER );
			actionStatusPanel.add( actionStatusLabel );
			actionStatusPanel.add( new JLabel( " ", JLabel.CENTER ) );

			loginnameField = client == null || getProperty( "saveState" ).equals( "" ) ?
				(JComponent)(new JTextField()) : (JComponent)(new LoginNameComboBox());

			passwordField = new JPasswordField();
			savePasswordCheckBox = new JCheckBox();
			savePasswordCheckBox.addActionListener( this );

			autoLoginCheckBox = new JCheckBox();
			getBreakfastCheckBox = new JCheckBox();

			JPanel checkBoxPanels = new JPanel();
			checkBoxPanels.setLayout( new BoxLayout( checkBoxPanels, BoxLayout.X_AXIS ) );
			checkBoxPanels.add( Box.createHorizontalStrut( 20 ) );
			checkBoxPanels.add( new JLabel( "Save Password: " ), "" );
			checkBoxPanels.add( savePasswordCheckBox );
			checkBoxPanels.add( Box.createHorizontalStrut( 20 ) );
			checkBoxPanels.add( new JLabel( "Auto-Login: " ), "" );
			checkBoxPanels.add( autoLoginCheckBox );
			checkBoxPanels.add( Box.createHorizontalStrut( 20 ) );
			checkBoxPanels.add( new JLabel( "Get Breakfast: " ), "" );
			checkBoxPanels.add( getBreakfastCheckBox );

			JPanel southPanel = new JPanel();
			southPanel.setLayout( new BorderLayout( 10, 10 ) );
			southPanel.add( checkBoxPanels, BorderLayout.NORTH );
			southPanel.add( new JPanel(), BorderLayout.CENTER );
			southPanel.add( actionStatusPanel, BorderLayout.SOUTH );

			VerifiableElement [] elements = new VerifiableElement[2];
			elements[0] = new VerifiableElement( "Login: ", loginnameField );
			elements[1] = new VerifiableElement( "Password: ", passwordField );

			setContent( elements );
			add( southPanel, BorderLayout.SOUTH );

			if ( client != null )
			{
				String autoLoginSetting = getProperty( "autoLogin" );

				if ( loginnameField instanceof JComboBox )
					((JComboBox)loginnameField).setSelectedItem( autoLoginSetting );

				String passwordSetting = client.getSaveState( autoLoginSetting );

				if ( passwordSetting != null )
				{
					passwordField.setText( passwordSetting );
					savePasswordCheckBox.setSelected( true );
					autoLoginCheckBox.setSelected( true );
				}
			}

			setDefaultButton( confirmedButton );
		}

		public void setStatusMessage( int displayState, String s )
		{	actionStatusLabel.setText( s );
		}

		public void setEnabled( boolean isEnabled )
		{
			super.setEnabled( isEnabled );
			loginnameField.setEnabled( isEnabled );
			passwordField.setEnabled( isEnabled );
			savePasswordCheckBox.setEnabled( isEnabled );
			autoLoginCheckBox.setEnabled( isEnabled );
			getBreakfastCheckBox.setEnabled( isEnabled );
		}

		protected void actionConfirmed()
		{	login( false );
		}

		protected void actionCancelled()
		{
			if ( loginnameField.isEnabled() )
				login( true );
			else
			{
				client.updateDisplay( CANCELLED_STATE, "Login cancelled." );
				client.cancelRequest();
				requestFocus();
			}
		}

		private void login( boolean isQuickLogin )
		{
			String loginname = ((String)(loginnameField instanceof JComboBox ?
				((JComboBox)loginnameField).getSelectedItem() : ((JTextField)loginnameField).getText() ));

			String password = new String( passwordField.getPassword() );

			if ( loginname == null || password == null || loginname.equals("") || password.equals("") )
			{
				client.updateDisplay( ERROR_STATE, "Invalid login." );
				return;
			}

			if ( autoLoginCheckBox.isSelected() )
				setProperty( "autoLogin", loginname );
			else
				setProperty( "autoLogin", "" );

			if ( isQuickLogin && !loginname.endsWith( "/q" ) )
				loginname += "/q";

			client.updateDisplay( DISABLED_STATE, "Determining login settings..." );
			(new LoginRequest( client, loginname, password, getBreakfastCheckBox.isSelected(), savePasswordCheckBox.isSelected(), isQuickLogin )).run();
		}

		public void requestFocus()
		{	loginnameField.requestFocus();
		}

		public void actionPerformed( ActionEvent e )
		{
			if ( !savePasswordCheckBox.isSelected() && loginnameField instanceof JComboBox )
				client.removeSaveState( (String) ((JComboBox)loginnameField).getSelectedItem() );
		}

		/**
		 * Special instance of a JComboBox which overrides the default
		 * key events of a JComboBox to allow you to catch key events.
		 */

		private class LoginNameComboBox extends JComboBox implements FocusListener
		{
			private String currentName;
			private String currentMatch;

			public LoginNameComboBox()
			{
				super( saveStateNames );
				this.setEditable( true );
				this.getEditor().getEditorComponent().addFocusListener( this );
				this.getEditor().getEditorComponent().addKeyListener( new NameInputListener() );
			}

			public void setSelectedItem( Object anObject )
			{
				super.setSelectedItem( anObject );
				currentMatch = (String) anObject;
				setPassword();
			}

			public void focusGained( FocusEvent e )
			{
				getEditor().selectAll();
				findMatch( KeyEvent.VK_DELETE );
			}

			public void focusLost( FocusEvent e )
			{
				if ( currentName == null || currentName.trim().length() == 0 )
					return;

				if ( currentMatch == null && !saveStateNames.contains( currentName ) )
				{
					saveStateNames.add( currentName );
					currentMatch = currentName;
				}

				setSelectedItem( currentMatch );
				setPassword();
				hidePopup();
			}

			private void setPassword()
			{
				if ( currentMatch == null )
				{
					passwordField.setText( "" );
					savePasswordCheckBox.setSelected( false );
				}

				String password = client.getSaveState( currentMatch );
				if ( password != null )
				{
					passwordField.setText( password );
					savePasswordCheckBox.setSelected( true );
				}
				else
				{
					passwordField.setText( "" );
					savePasswordCheckBox.setSelected( false );
				}
			}

			private void findMatch( int keycode )
			{
				// If it wasn't the enter key that was being released,
				// then make sure that the current name is stored
				// before the key typed event is fired

				currentName = ((String) getEditor().getItem()).trim();
				currentMatch = null;

				// Autohighlight and popup - note that this
				// should only happen for standard typing
				// keys, or the delete and backspace keys.

				boolean matchNotFound = true;
				Object [] currentNames = saveStateNames.toArray();

				if ( currentName.length() > 0 )
				{
					for ( int i = 0; i < currentNames.length && matchNotFound; ++i )
					{
						if ( ((String)currentNames[i]).toLowerCase().startsWith( currentName.toLowerCase() ) )
						{
							showPopup();
							matchNotFound = false;

							if ( ((String)currentNames[i]).toLowerCase().equals( currentName.toLowerCase() ) )
								setSelectedIndex(i);

							if ( keycode == KeyEvent.VK_BACK_SPACE || keycode == KeyEvent.VK_DELETE )
							{
								// If this was an undefined character, then it
								// was a backspace or a delete - in this case,
								// you retain the original name after selecting
								// the index.

								getEditor().setItem( currentName );
							}
							else
							{
								// If this wasn't an undefined character, then
								// the user wants autocompletion!  Highlight
								// the rest of the possible name.

								currentMatch = (String) currentNames[i];
								getEditor().setItem( currentMatch );
								JTextComponent editor = (JTextComponent) getEditor().getEditorComponent();
								editor.setSelectionStart( currentName.length() );
								editor.setSelectionEnd( currentMatch.length() );
							}
						}
					}
				}

				// In the event that no match was found (or the
				// user hasn't entered anything), there is no
				// need to enter the loop

				if ( matchNotFound )
					hidePopup();
			}

			private class NameInputListener extends KeyAdapter
			{
				public void keyReleased( KeyEvent e )
				{
					if ( e.getKeyCode() == KeyEvent.VK_ENTER )
					{
						passwordField.requestFocus();
						return;
					}
					else if ( e.getKeyChar() == KeyEvent.CHAR_UNDEFINED )
						return;

					findMatch( e.getKeyCode() );
				}
			}
		}
	}

	/**
	 * Formally exits the program if there are no active sessions when
	 * this frame is closed.
	 */

	private class ExitRequestAdapter extends WindowAdapter
	{
		public void windowClosed( WindowEvent e )
		{
			if ( client == null || client.inLoginState() )
				System.exit( 0 );
		}
	}

	public static void main( String [] args )
	{
		Object [] parameters = new Object[2];
		parameters[0] = null;
		parameters[1] = new SortedListModel();

		(new CreateFrameRunnable( LoginFrame.class, parameters )).run();
	}
}
