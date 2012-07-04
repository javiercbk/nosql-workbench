/*******************************************************************************
 * Copyright 2012 Javier Ignacio Lecuona
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.sube.exceptions.card;

import com.sube.beans.SubeCard;

public abstract class SubeCardException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2902561240806424967L;
	protected SubeCard causedBy;

	public SubeCard getCausedBy() {
		return causedBy;
	}

	public void setCausedBy(SubeCard causedBy) {
		this.causedBy = causedBy;
	}
}
