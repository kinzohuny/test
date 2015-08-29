package com.jiuqi.dna.core.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.jiuqi.dna.core.TreeNodeFilter;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.resource.ResourceTokenLink;

final class CacheTree<TFacade, TImplement extends TFacade, TKeysHolder> {

	CacheTree(final CacheGroup<TFacade, TImplement, TKeysHolder> bindGroup) {
		if (bindGroup == null) {
			throw new UnsupportedOperationException();
		}
		final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		this.readLock = lock.readLock();
		this.writeLock = lock.writeLock();
		this.modifiedNodes = null;
		this.bindGroup = bindGroup;
	}

	final void dispose() {
		this.writeLock.lock();
		try {
			Node.whenTreeDisposedDisposeChildNode(this.firstRoot);
			this.modifiedNodes = null;
			this.firstRoot = null;
		} finally {
			this.writeLock.unlock();
		}
	}

	final CacheHolder<TFacade, TImplement, TKeysHolder> tryGetParentOf(
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
			final Transaction transaction) {
		this.readLock.lock();
		try {
			CacheTree.Node<TFacade, TImplement, TKeysHolder> node = holder.findTreeNodeIn(transaction, false);
			node = node == null ? null : node.getParent(this.bindGroup.isModifiableOnTransaction(transaction));
			return node == null ? null : node.holder;
		} finally {
			this.readLock.unlock();
		}
	}

	final ResourceTokenLink<TFacade> tryGetChildrenOf(
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
			final Transaction transaction) {
		this.readLock.lock();
		try {
			CacheTree.Node<TFacade, TImplement, TKeysHolder> node = holder.findTreeNodeIn(transaction, false);
			if (node == null) {
				return null;
			} else {
				final ResourceTokenLinkImplement<TFacade> first = new ResourceTokenLinkImplement<TFacade>(holder);
				ResourceTokenLinkImplement<TFacade> last = first;
				while ((node = node.nextSibling) != null) {
					last.next = new ResourceTokenLinkImplement<TFacade>((node.holder));
					last = last.next;
				}
				return first;
			}
		} finally {
			this.readLock.unlock();
		}
	}

	/**
	 * @return 返回null表示缓存树已经被销毁
	 */
	final TreeNodeImpl<TFacade> tryGetTreeValue(
			final TreeNodeFilter<? super TFacade> filter,
			final Comparator<? super TFacade> sortComparator,
			final Transaction transaction) {
		final TreeNodeImpl<TFacade> root = new TreeNodeImpl<TFacade>(null, null);
		this.bindGroup.ensureInitialized(transaction);
		this.readLock.lock();
		try {
			if (this.bindGroup.isDisposed()) {
				return null;
			} else {
				if (this.firstRoot != null) {
					if (this.bindGroup.isModifiableOnTransaction(transaction)) {
						Node.getChildNodeValueByExclusiveThread(root, this.firstRoot, transaction);
					} else {
						Node.getChildNodeValue(root, this.firstRoot, transaction);
					}
				}
			}
		} finally {
			this.readLock.unlock();
		}
		if (root.getChildCount() != 0) {
			root.filterAndSortRecursively(filter, 0, 0, sortComparator);
		}
		return root;
	}

	/**
	 * @return 返回null表示缓存树已经被销毁或根缓存项已经被销毁
	 */
	@SuppressWarnings("unchecked")
	final TreeNodeImpl<TFacade> tryGetTreeValue(
			final TreeNodeFilter<? super TFacade> filter,
			final Comparator<? super TFacade> sortComparator,
			final CacheHolder<TFacade, ?, ?> rootHolder,
			final Transaction transaction) {
		final TreeNodeImpl<TFacade> rootNode;
		int absoluteLevel;
		this.bindGroup.ensureInitialized(transaction);
		this.readLock.lock();
		try {
			if (this.bindGroup.isDisposed()) {
				return null;
			} else {
				final Node<TFacade, TImplement, TKeysHolder> root = ((CacheHolder<TFacade, TImplement, TKeysHolder>) rootHolder).findTreeNodeIn(transaction, false);
				if (root == null) {
					return new TreeNodeImpl<TFacade>(null, null);
				} else {
					final TFacade value = rootHolder.tryGetValue(transaction);
					if (value == null) {
						return null;
					}
					rootNode = new TreeNodeImpl<TFacade>(null, value);
					if (root.firstChild != null) {
						if (this.bindGroup.isModifiableOnTransaction(transaction)) {
							Node.getChildNodeValueByExclusiveThread(rootNode, root.firstChild, transaction);
						} else {
							Node.getChildNodeValue(rootNode, root.firstChild, transaction);
						}
					}
					absoluteLevel = 1;
					Node<TFacade, TImplement, TKeysHolder> node = root;
					while (node.parent != null) {
						absoluteLevel++;
						node = node.parent;
					}
				}
			}
		} finally {
			this.readLock.unlock();
		}
		rootNode.filterAndSortRecursively(filter, absoluteLevel, 0, sortComparator);
		return rootNode;
	}

	/**
	 * @return 返回null表示缓存树已经被销毁
	 */
	final TreeNodeImpl<TFacade> tryGetTreeValue(
			final AccessController accessController,
			final Operation<?> operation,
			final TreeNodeFilter<? super TFacade> filter,
			final Comparator<? super TFacade> sortComparator,
			final Transaction transaction) {
		if (!this.bindGroup.define.isAccessControlDefine()) {
			throw new UnsupportedAccessControlException(this.bindGroup.define.facadeClass);
		}
		final TreeNodeImpl<TFacade> root = new TreeNodeImpl<TFacade>(null, null);
		this.bindGroup.ensureInitialized(transaction);
		this.readLock.lock();
		try {
			if (this.bindGroup.isDisposed()) {
				return null;
			} else {
				if (this.firstRoot != null) {
					// final ContextImpl<?, ?, ?> context = transaction
					// .getCurrentContext();
					// final ResourceServiceBase<?, ?, ?> resourceService =
					// this.bindGroup.define.resourceService;
					// resourceService.callBeforeAccessAuthorityResource(context);
					// try {
					final OperationEntry operationEntry = OperationEntry.operationEntryOf(operation, this.bindGroup.define.accessControlDefine.operationEntrys);
					final boolean defaultAuthority = this.bindGroup.define.accessControlDefine.defaultAuthority;
					if (this.bindGroup.isModifiableOnTransaction(transaction)) {
						Node.getChildNodeValueByExclusiveThread_ForRefuseFirst(accessController, operationEntry, defaultAuthority, root, this.firstRoot, transaction);
					} else {
						Node.getChildNodeValue_ForRefuseFirst(accessController, operationEntry, defaultAuthority, root, this.firstRoot, transaction);
					}
					// } finally {
					// resourceService.callEndAccessAuthorityResource(context);
					// }
				}
			}
		} finally {
			this.readLock.unlock();
		}
		if (root.getChildCount() != 0) {
			root.filterAndSortRecursively(filter, 0, 0, sortComparator);
		}
		return root;
	}

	/**
	 * @return 返回null表示缓存树已经被销毁或根缓存项已经被销毁
	 */
	@SuppressWarnings("unchecked")
	final TreeNodeImpl<TFacade> tryGetTreeValue(
			final AccessController accessController,
			final Operation<?> operation,
			final TreeNodeFilter<? super TFacade> filter,
			final Comparator<? super TFacade> sortComparator,
			final CacheHolder<TFacade, ?, ?> rootHolder,
			final Transaction transaction) {
		if (!this.bindGroup.define.isAccessControlDefine()) {
			throw new UnsupportedAccessControlException(this.bindGroup.define.facadeClass);
		}
		final TreeNodeImpl<TFacade> rootNode;
		int absoluteLevel;
		this.bindGroup.ensureInitialized(transaction);
		this.readLock.lock();
		try {
			if (this.bindGroup.isDisposed()) {
				return null;
			} else {
				final Node<TFacade, TImplement, TKeysHolder> root = ((CacheHolder<TFacade, TImplement, TKeysHolder>) rootHolder).findTreeNodeIn(transaction, false);
				if (root == null) {
					return new TreeNodeImpl<TFacade>(null, null);
				} else {
					// final ContextImpl<?, ?, ?> context = transaction
					// .getCurrentContext();
					// final ResourceServiceBase<?, ?, ?> resourceService =
					// this.bindGroup.define.resourceService;
					// resourceService.callBeforeAccessAuthorityResource(context);
					// try {
					final TFacade value = rootHolder.tryGetValue(transaction);
					if (value == null) {
						return null;
					}
					rootNode = new TreeNodeImpl<TFacade>(null, value);
					final OperationEntry operationEntry = OperationEntry.operationEntryOf(operation, rootHolder.ownGroup.define.accessControlDefine.operationEntrys);
					final boolean defaultAuthority = accessController.internalHasAuthority(operationEntry, rootHolder.asAccessControlHolder());
					if (root.firstChild != null) {
						if (this.bindGroup.isModifiableOnTransaction(transaction)) {
							Node.getChildNodeValueByExclusiveThread_ForRefuseFirst(accessController, operationEntry, defaultAuthority, rootNode, root.firstChild, transaction);
						} else {
							Node.getChildNodeValue_ForRefuseFirst(accessController, operationEntry, defaultAuthority, rootNode, root.firstChild, transaction);
						}
					}
					absoluteLevel = 1;
					Node<TFacade, TImplement, TKeysHolder> node = root;
					while (node.parent != null) {
						absoluteLevel++;
						node = node.parent;
					}
					// } finally {
					// resourceService.callEndAccessAuthorityResource(context);
					// }
				}
			}
		} finally {
			this.readLock.unlock();
		}
		rootNode.filterAndSortRecursively(filter, absoluteLevel, 0, sortComparator);
		return rootNode;
	}

	final void localTryCreateNode(
			final CacheHolder<TFacade, TImplement, TKeysHolder> parent,
			final CacheHolder<TFacade, TImplement, TKeysHolder> child,
			final Transaction transaction) {
		transaction.handleAcquirable(this.bindGroup, AcquireFor.MODIFY_ITEMS);
		transaction.handleAcquirable(child, AcquireFor.MODIFY_ITEMS);
		final Node<TFacade, TImplement, TKeysHolder> modifiedNode;
		this.bindGroup.ensureInitialized(transaction);
		this.writeLock.lock();
		try {
			if (this.bindGroup.isDisposed() || child.isDisposed()) {
				return;
			} else {
				final Node<TFacade, TImplement, TKeysHolder> parentNode;
				if (parent == null) {
					parentNode = null;
				} else {
					final Node<TFacade, TImplement, TKeysHolder> parentOwnTreeNode = parent.findTreeNodeIn(transaction, true);
					if (parentOwnTreeNode.asTempNode() == null) {
						parentNode = parentOwnTreeNode;
					} else {
						parentNode = parentOwnTreeNode.asTempNode().original();
					}
				}
				final Node<TFacade, TImplement, TKeysHolder> childNode = child.findTreeNodeIn(transaction, false);
				if (childNode == null) {
					final Node<TFacade, TImplement, TKeysHolder> node = child.forceGetOwnTreeNode();
					if (node == null) {
						// 新建节点
						modifiedNode = new Node<TFacade, TImplement, TKeysHolder>(this, child, parentNode);
					} else {
						// 刚从树上移走
						if (node.parent == parentNode) {
							// 移回原来的位置
							node.resolve();
						} else {
							if (parentNode == null) {
								this.addRoot(node.new TempNode(null));
							} else {
								parentNode.addChild(node.new TempNode(parentNode));
							}
						}
						modifiedNode = null;
					}
				} else if (childNode.parent != parentNode) {
					// 当前节点不在新父节点下
					if (childNode.state == Node.STATE_CREATED) {
						// 将新建状态的节点移动到新父节点下
						if (childNode.parent == null) {
							this.exciseRoot(childNode);
						} else {
							childNode.parent.exciseChild(childNode);
						}
						final Node<TFacade, TImplement, TKeysHolder>.TempNode tempNode = childNode.asTempNode();
						if (tempNode != null && tempNode.original().parent == parentNode) {
							// 被移动的节点是个临时节点，且移回原位
							tempNode.original().resolve();
						} else {
							if (parentNode == null) {
								this.addRoot(childNode);
							} else {
								parentNode.addChild(childNode);
							}
						}
						modifiedNode = null;
					} else {
						final Node<TFacade, TImplement, TKeysHolder>.TempNode tempNode = childNode.new TempNode(parentNode);
						if (parentNode == null) {
							this.addRoot(childNode.new TempNode(parentNode));
						} else {
							parentNode.addChild(tempNode);
						}
						modifiedNode = childNode;
					}
				} else {
					modifiedNode = null;
				}
			}
		} finally {
			this.writeLock.unlock();
		}
		if (modifiedNode != null) {
			this.addModifiedNode(modifiedNode);
		}
	}

	final void localTryRemoveNode(
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
			final Transaction transaction) {
		transaction.handleAcquirable(this.bindGroup, AcquireFor.MODIFY_ITEMS);
		transaction.handleAcquirable(holder, AcquireFor.MODIFY);
		if (this.bindGroup.isDisposed() || holder.isDisposed()) {
			return;
		} else {
			final Node<TFacade, TImplement, TKeysHolder> node = holder.findTreeNodeIn(transaction, false);
			if (node == null) {
				return;
			} else {
				if (node.firstChild != null) {
					Node.lockChildNode(node.firstChild, transaction);
				}
				Node.remove(node);
			}
		}
	}

	final void localCreateNodeAndCommit(
			final CacheHolder<TFacade, TImplement, TKeysHolder> parent,
			final CacheHolder<TFacade, TImplement, TKeysHolder> child) {
		this.internalCreateNodeAndCommit(parent, child);
	}

	final void localCreateNodeWhenInitialize(
			final CacheHolder<TFacade, TImplement, TKeysHolder> parent,
			final CacheHolder<TFacade, TImplement, TKeysHolder> child) {
		Node<TFacade, TImplement, TKeysHolder> childNode = null;
		if (parent == null) {
			childNode = child.findTreeNodeIn(true, false);
			if (childNode == null) {
				childNode = new Node<TFacade, TImplement, TKeysHolder>(this, child, null);
			} else if (childNode.parent != null) {
				childNode.parent.exciseChild(childNode);
				this.addRoot(childNode);
			}
		} else {
			final Node<TFacade, TImplement, TKeysHolder> parentNode = parent.findTreeNodeIn(true, true);
			childNode = child.findTreeNodeIn(true, false);
			if (childNode == null) {
				childNode = new Node<TFacade, TImplement, TKeysHolder>(this, child, parentNode);
			} else if (childNode.parent == null) {
				this.exciseRoot(childNode);
				parentNode.addChild(childNode);
			} else if (childNode.parent != parentNode) {
				childNode.parent.exciseChild(childNode);
				parentNode.addChild(childNode);
			}
		}
		if (childNode != null) {
			this.addModifiedNode(childNode);
		}
	}

	final void remoteCreateNode(
			final CacheHolder<TFacade, TImplement, TKeysHolder> parent,
			final CacheHolder<TFacade, TImplement, TKeysHolder> child,
			final Transaction transaction) {
		Node<TFacade, TImplement, TKeysHolder> modifiedNode = null;
		this.writeLock.lock();
		try {
			if (parent == null) {
				final Node<TFacade, TImplement, TKeysHolder> childNode = child.findTreeNodeIn(transaction, false);
				if (childNode == null) {
					// 新建一级节点
					modifiedNode = new Node<TFacade, TImplement, TKeysHolder>(this, child, null);
				} else if (childNode.getParent(this.bindGroup.isModifiableOnTransaction(transaction)) != null) {
					// 当前节点不是一级节点
					this.addRoot(childNode.new TempNode(null));
					modifiedNode = childNode;
				}
			} else {
				Node<TFacade, TImplement, TKeysHolder> parentNode = parent.findTreeNodeIn(transaction, false);
				if (parentNode == null) {
					parentNode = new Node<TFacade, TImplement, TKeysHolder>(this, parent, null);
					this.addModifiedNode(parentNode);
				} else if (parentNode.asTempNode() != null) {
					parentNode = parentNode.asTempNode().original();
				}
				final Node<TFacade, TImplement, TKeysHolder> childNode = child.findTreeNodeIn(transaction, false);
				if (childNode == null) {
					// 新建非一级节点
					modifiedNode = new Node<TFacade, TImplement, TKeysHolder>(this, child, parentNode);
				} else if (childNode.getParent(this.bindGroup.isModifiableOnTransaction(transaction)) != parentNode) {
					// 当前节点的父节点不是指定的父节点
					parentNode.addChild(childNode.new TempNode(parentNode));
					modifiedNode = childNode;
				}
			}
		} finally {
			this.writeLock.unlock();
		}
		if (modifiedNode != null) {
			this.addModifiedNode(modifiedNode);
		}
	}

	final void remoteRemoveNode(
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
			final Transaction transaction) {
		final Node<TFacade, TImplement, TKeysHolder> node = holder.findTreeNodeIn(transaction, false);
		if (node != null) {
			// this.writeLock.lock();
			// try {
			Node.remove(node);
			// } finally {
			// this.writeLock.unlock();
			// }
			// this.addModifiedNode(node);
		}
	}

	final void remoteCreateNodeAndCommit(
			final CacheHolder<TFacade, TImplement, TKeysHolder> parent,
			final CacheHolder<TFacade, TImplement, TKeysHolder> child) {
		this.internalCreateNodeAndCommit(parent, child);
	}

	final void onTransactionCommit(final Transaction transaction) {
		if (this.modifiedNodes != null) {
			this.writeLock.lock();
			try {
				for (Node<TFacade, TImplement, TKeysHolder> modifiedNode : this.modifiedNodes) {
					if (modifiedNode.holder instanceof AccessControlCacheHolder) {
						((AccessControlCacheHolder<TFacade, TImplement, TKeysHolder>) (modifiedNode.holder)).resetCachedNextHolderInAuthorityInheritPath();
					}
					switch (modifiedNode.state) {
					case Node.STATE_RESOLVED:
						this.tryResoleTempNodeOf(modifiedNode, false);
						continue;
					case Node.STATE_CREATED:
						modifiedNode.resolve();
						continue;
					case Node.STATE_REMOVED:
						this.tryResoleTempNodeOf(modifiedNode, true);
					case Node.STATE_DISPOSED:
						modifiedNode.dispose();
						continue;
					default:
						throw new UnsupportedOperationException();
					}
				}
			} finally {
				this.writeLock.unlock();
				this.modifiedNodes = null;
			}
		}
	}

	final void onTransactionRollback(final Transaction transaction) {
		if (this.modifiedNodes != null) {
			this.writeLock.lock();
			try {
				for (Node<TFacade, TImplement, TKeysHolder> modifiedNode : this.modifiedNodes) {
					switch (modifiedNode.state) {
					case Node.STATE_RESOLVED:
						this.tryDisposeTempNodeOf(modifiedNode);
						continue;
					case Node.STATE_REMOVED:
						this.tryDisposeTempNodeOf(modifiedNode);
						modifiedNode.resolve();
						continue;
					case Node.STATE_CREATED:
					case Node.STATE_DISPOSED:
						modifiedNode.dispose();
						continue;
					default:
						throw new UnsupportedOperationException();
					}
				}
			} finally {
				this.writeLock.unlock();
				this.modifiedNodes = null;
			}
		}
	}

	final void collectTreeData(final CacheInitializeCollector collector) {
		if (this.firstRoot != null) {
			Node.collectChildNodeTreeData(collector, this.firstRoot, null);
		}
	}

	final void collectModifiedTreeData(final CacheSynchronizeCollector collector) {
		if (this.modifiedNodes != null) {
			for (Node<TFacade, TImplement, TKeysHolder> modifiedNode : this.modifiedNodes) {
				if (modifiedNode.tempNode == null) {
					if (modifiedNode.state == Node.STATE_CREATED) {
						Node<TFacade, TImplement, TKeysHolder> parentNode = modifiedNode.parent;
						collector.addCreateTreeNodeData(parentNode == null ? null : parentNode.holder, modifiedNode.holder);
					}
				} else {
					final Node<TFacade, TImplement, TKeysHolder> newParentNode = modifiedNode.tempNode.parent;
					collector.addCreateTreeNodeData(newParentNode == null ? null : newParentNode.holder, modifiedNode.holder);
				}
			}
		}
	}

	private final void addRoot(final Node<TFacade, TImplement, TKeysHolder> root) {
		if (this.firstRoot == null) {
			this.firstRoot = root;
			root.lastSibling = null;
			root.nextSibling = null;
		} else {
			if (this.firstRoot.nextSibling == null) {
				this.firstRoot.nextSibling = root;
				root.nextSibling = null;
				root.lastSibling = this.firstRoot;
			} else {
				root.nextSibling = this.firstRoot.nextSibling;
				this.firstRoot.nextSibling = root;
				root.nextSibling.lastSibling = root;
				root.lastSibling = this.firstRoot;
			}
		}
	}

	private final void exciseRoot(
			final Node<TFacade, TImplement, TKeysHolder> root) {
		final Node<TFacade, TImplement, TKeysHolder> firstRoot = this.firstRoot;
		if (firstRoot == root) {
			if (root.nextSibling == null) {
				this.firstRoot = null;
			} else {
				this.firstRoot = firstRoot.nextSibling;
				this.firstRoot.lastSibling = null;
			}
		} else {
			root.lastSibling.nextSibling = root.nextSibling;
			if (root.nextSibling != null) {
				root.nextSibling.lastSibling = root.lastSibling;
			}
		}
		root.nextSibling = null;
		root.lastSibling = null;
	}

	private final void tryResoleTempNodeOf(
			final Node<TFacade, TImplement, TKeysHolder> modifiedNode,
			final boolean ordinalRemoved) {
		final Node<TFacade, TImplement, TKeysHolder>.TempNode tempNode = modifiedNode.tempNode;
		if (tempNode != null) {
			if (ordinalRemoved) {
				final Node<TFacade, TImplement, TKeysHolder> newNode = tempNode.toNode();

				// 修改所有子节点的父
				Node<TFacade, TImplement, TKeysHolder> child = newNode.firstChild;
				while (child != null) {
					child.parent = newNode;
					child = child.nextSibling;
				}

				if (tempNode.parent == null) {
					this.exciseRoot(tempNode);
					this.addRoot(newNode);
				} else {
					final Node<TFacade, TImplement, TKeysHolder> newParent = tempNode.parent;
					tempNode.parent.exciseChild(tempNode);
					newParent.addChild(newNode);
				}
				newNode.resolve();
				newNode.holder.tryPutToTreeNode(newNode);
			} else {
				if (tempNode.parent == null) {
					this.exciseRoot(tempNode);
					// 如果tempNode.parent为空的话，则modifiedNode.parent肯定不为空
					modifiedNode.parent.exciseChild(modifiedNode);
					this.addRoot(modifiedNode);
				} else {
					final Node<TFacade, TImplement, TKeysHolder> newParent = tempNode.parent;
					tempNode.parent.exciseChild(tempNode);
					if (modifiedNode.parent == null) {
						this.exciseRoot(modifiedNode);
					} else {
						modifiedNode.parent.exciseChild(modifiedNode);
					}
					newParent.addChild(modifiedNode);
				}
			}
			modifiedNode.tempNode = null;
		}
	}

	private final void tryDisposeTempNodeOf(
			final Node<TFacade, TImplement, TKeysHolder> modifiedNode) {
		final Node<TFacade, TImplement, TKeysHolder> tempNode = modifiedNode.tempNode;
		if (tempNode != null) {
			if (tempNode.parent == null) {
				this.exciseRoot(tempNode);
			} else {
				tempNode.parent.exciseChild(tempNode);
			}
			modifiedNode.tempNode = null;
		}
	}

	private final void addModifiedNode(
			final Node<TFacade, TImplement, TKeysHolder> node) {
		if (this.modifiedNodes == null) {
			this.modifiedNodes = new ArrayList<Node<TFacade, TImplement, TKeysHolder>>();
		} else {
			if (this.modifiedNodes.contains(node)) {
				return;
			}
		}
		this.modifiedNodes.add(node);
	}

	private final void internalCreateNodeAndCommit(
			final CacheHolder<TFacade, TImplement, TKeysHolder> parent,
			final CacheHolder<TFacade, TImplement, TKeysHolder> child) {
		if (parent == null) {
			Node<TFacade, TImplement, TKeysHolder> childNode = child.findTreeNodeIn(true, false);
			if (childNode == null) {
				childNode = new Node<TFacade, TImplement, TKeysHolder>(this, child, null);
				childNode.state = Node.STATE_RESOLVED;
			} else if (childNode.parent != null) {
				childNode.parent.exciseChild(childNode);
				this.addRoot(childNode);
			}
		} else {
			final Node<TFacade, TImplement, TKeysHolder> parentNode = parent.findTreeNodeIn(true, true);
			Node<TFacade, TImplement, TKeysHolder> childNode = child.findTreeNodeIn(true, false);
			if (childNode == null) {
				childNode = new Node<TFacade, TImplement, TKeysHolder>(this, child, parentNode);
				childNode.state = Node.STATE_RESOLVED;
			} else if (childNode.parent == null) {
				this.exciseRoot(childNode);
				parentNode.addChild(childNode);
			} else if (childNode.parent != parentNode) {
				childNode.parent.exciseChild(childNode);
				parentNode.addChild(childNode);
			}
		}
	}

	final CacheGroup<TFacade, TImplement, TKeysHolder> bindGroup;

	private volatile Node<TFacade, TImplement, TKeysHolder> firstRoot;

	private final ReentrantReadWriteLock.ReadLock readLock;

	private final ReentrantReadWriteLock.WriteLock writeLock;

	private ArrayList<Node<TFacade, TImplement, TKeysHolder>> modifiedNodes;

	static class Node<TFacade, TImplement extends TFacade, TKeysHolder> {

		static final byte STATE_CREATED = 0;

		static final byte STATE_RESOLVED = 1;

		static final byte STATE_REMOVED = 2;

		static final byte STATE_DISPOSED = 3;

		private static final <TFacade, TImplement extends TFacade, TKeysHolder> void getChildNodeValueByExclusiveThread(
				final TreeNodeImpl<TFacade> parentNode,
				final Node<TFacade, TImplement, TKeysHolder> firstChild,
				final Transaction transaction) {
			Node<TFacade, TImplement, TKeysHolder> child = firstChild;
			while (child != null) {
				Node<TFacade, TImplement, TKeysHolder> node = child;
				if (node.state == Node.STATE_CREATED || (node.state == Node.STATE_RESOLVED && node.tempNode == null)) {
					final Node<TFacade, TImplement, TKeysHolder>.TempNode tempNode = node.asTempNode();
					if (tempNode != null) {
						node = tempNode.original();
					}
					final TImplement value = node.holder.tryGetValue(transaction);
					if (value != null) {
						final TreeNodeImpl<TFacade> childNode = parentNode.append(value);
						if (node.firstChild != null) {
							getChildNodeValueByExclusiveThread(childNode, node.firstChild, transaction);
						}
					}
				}
				child = child.nextSibling;
			}
		}

		private static final <TFacade, TImplement extends TFacade, TKeysHolder> void getChildNodeValue(
				final TreeNodeImpl<TFacade> parentNode,
				final Node<TFacade, TImplement, TKeysHolder> firstChild,
				final Transaction transaction) {
			Node<TFacade, TImplement, TKeysHolder> child = firstChild;
			while (child != null) {
				Node<TFacade, TImplement, TKeysHolder> node = child;
				if (node.state == Node.STATE_RESOLVED || node.state == Node.STATE_REMOVED) {
					final TImplement value = node.holder.tryGetValue(transaction);
					if (value != null) {
						final TreeNodeImpl<TFacade> childNode = parentNode.append(value);
						if (node.firstChild != null) {
							getChildNodeValue(childNode, node.firstChild, transaction);
						}
					}
				}
				child = child.nextSibling;
			}
		}

		private static final <TFacade, TImplement extends TFacade, TKeysHolder> void getChildNodeValueByExclusiveThread_ForRefuseFirst(
				final AccessController accessController,
				final OperationEntry operation, final boolean defaultAuthority,
				final TreeNodeImpl<TFacade> parentNode,
				final Node<TFacade, TImplement, TKeysHolder> firstChild,
				final Transaction transaction) {
			Node<TFacade, TImplement, TKeysHolder> child = firstChild;
			while (child != null) {
				Node<TFacade, TImplement, TKeysHolder> node = child;
				if (node.state == Node.STATE_CREATED || (node.state == Node.STATE_RESOLVED && node.tempNode == null)) {
					final Node<TFacade, TImplement, TKeysHolder>.TempNode tempNode = node.asTempNode();
					if (tempNode != null) {
						node = tempNode.original();
					}
					final boolean authority = accessController.internalHasAuthority(operation, node.holder.asAccessControlHolder(), defaultAuthority);
					final TreeNodeImpl<TFacade> childNode;
					tryAddValue: {
						if (authority) {
							final TImplement value = node.holder.tryGetValue(transaction);
							if (value == null) {
								break tryAddValue;
							}
							childNode = parentNode.append(value);
						} else {
							childNode = parentNode.append(null);
						}
						if (node.firstChild != null) {
							getChildNodeValueByExclusiveThread_ForRefuseFirst(accessController, operation, authority, childNode, node.firstChild, transaction);
						}
					}
				}
				child = child.nextSibling;
			}
		}

		private static final <TFacade, TImplement extends TFacade, TKeysHolder> void getChildNodeValue_ForRefuseFirst(
				final AccessController accessController,
				final OperationEntry operation, final boolean defaultAuthority,
				final TreeNodeImpl<TFacade> parentNode,
				final Node<TFacade, TImplement, TKeysHolder> firstChild,
				final Transaction transaction) {
			Node<TFacade, TImplement, TKeysHolder> child = firstChild;
			while (child != null) {
				Node<TFacade, TImplement, TKeysHolder> node = child;
				if (node.state == Node.STATE_RESOLVED || node.state == Node.STATE_REMOVED) {
					final boolean authority = accessController.internalHasAuthority(operation, node.holder.asAccessControlHolder(), defaultAuthority);
					final TreeNodeImpl<TFacade> childNode;
					tryAddValue: {
						if (authority) {
							final TImplement value = node.holder.tryGetValue(transaction);
							if (value == null) {
								break tryAddValue;
							}
							childNode = parentNode.append(value);
						} else {
							childNode = parentNode.append(null);
						}
						if (node.firstChild != null) {
							getChildNodeValue_ForRefuseFirst(accessController, operation, authority, childNode, node.firstChild, transaction);
						}
					}
				}
				child = child.nextSibling;
			}
		}

		private static final void lockChildNode(final Node<?, ?, ?> firstChild,
				final Transaction transaction) {
			if (firstChild.tempNode != null) {
				return;
			}
			Node<?, ?, ?> child = firstChild;
			while (child != null) {
				transaction.handleAcquirable(child.holder, AcquireFor.MODIFY);
				final Node<?, ?, ?>.TempNode tempNode = child.asTempNode();
				if (tempNode != null) {
					Node<?, ?, ?> ordinal = tempNode.original();
					if (ordinal.firstChild != null) {
						lockChildNode(ordinal.firstChild, transaction);
					}
				} else {
					if (child.firstChild != null) {
						lockChildNode(child.firstChild, transaction);
					}
				}
				child = child.nextSibling;
			}
		}

		private static final void collectChildNodeTreeData(
				final CacheInitializeCollector collector,
				final Node<?, ?, ?> firstChild,
				final CacheHolder<?, ?, ?> parentItem) {
			Node<?, ?, ?> currentNode = firstChild;
			while (currentNode != null) {
				collector.addCreateTreeNodeData(parentItem, currentNode.holder);
				if (currentNode.firstChild != null) {
					collectChildNodeTreeData(collector, currentNode.firstChild, currentNode.holder);
				}
				currentNode = currentNode.nextSibling;
			}
		}

		private static final <TFacade, TImplement extends TFacade, TKeysHolder> void whenTreeDisposedDisposeChildNode(
				final Node<TFacade, TImplement, TKeysHolder> firstChild) {
			Node<TFacade, TImplement, TKeysHolder> child = firstChild;
			while (child != null) {
				child.state = STATE_DISPOSED;
				if (child.firstChild != null) {
					whenTreeDisposedDisposeChildNode(child.firstChild);
				}
				child = child.nextSibling;
			}
		}

		private static final <TFacade, TImplement extends TFacade, TKeysHolder> void remove(
				Node<TFacade, TImplement, TKeysHolder> root) {
			if (root.tempNode != null) {
				return;
			}
			final Node<TFacade, TImplement, TKeysHolder>.TempNode tempNode = root.asTempNode();
			if (tempNode != null) {
				tempNode.ownTree.writeLock.lock();
				try {
					if (tempNode.parent == null) {
						tempNode.ownTree.exciseRoot(tempNode);
					} else {
						tempNode.parent.exciseChild(tempNode);
					}
				} finally {
					tempNode.ownTree.writeLock.unlock();
				}
				root = tempNode.original();
				root.tempNode = null;
			}
			if (root.state == STATE_CREATED) {
				root.state = STATE_DISPOSED;
			} else {
				root.state = STATE_REMOVED;
				root.ownTree.addModifiedNode(root);
			}
			Node<TFacade, TImplement, TKeysHolder> child = root.firstChild;
			while (child != null) {
				remove(child);
				child = child.nextSibling;
			}
		}

		private Node(final CacheTree<TFacade, TImplement, TKeysHolder> ownTree,
				final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
				final Node<TFacade, TImplement, TKeysHolder> parent) {
			this.ownTree = ownTree;
			this.holder = holder;
			this.parent = parent;
			if (parent == null) {
				this.ownTree.addRoot(this);
			} else {
				parent.addChild(this);
			}
			this.state = STATE_CREATED;
			holder.tryPutToTreeNode(this);
		}

		/**
		 * 专用于构造TempNode
		 */
		private Node(final Node<TFacade, TImplement, TKeysHolder> newParent,
				final Node<TFacade, TImplement, TKeysHolder> ordinal) {
			this.parent = newParent;
			this.holder = ordinal.holder;
			this.ownTree = ordinal.ownTree;
		}

		final void whenItemDisposedDispose() {
			if (this.state == STATE_DISPOSED) {
				return;
			}
			this.ownTree.writeLock.lock();
			try {
				this.state = STATE_DISPOSED;
				if (this.parent == null) {
					this.ownTree.exciseRoot(this);
				} else {
					this.parent.exciseChild(this);
				}
				if (this.tempNode != null) {
					if (this.tempNode.parent == null) {
						this.ownTree.exciseRoot(this.tempNode);
					} else {
						this.tempNode.parent.exciseChild(this.tempNode);
					}
				}
			} finally {
				this.ownTree.writeLock.unlock();
			}
		}

		final byte getState() {
			return this.state;
		}

		// final void forceSetState(final byte state) {
		// this.state = state;
		// }

		final Node<TFacade, TImplement, TKeysHolder> getParent(
				final boolean currentExclusiveThread) {
			if (this.tempNode != null && currentExclusiveThread) {
				return this.tempNode.parent;
			} else {
				return this.parent;
			}
		}

		TempNode asTempNode() {
			return null;
		}

		Node<TFacade, TImplement, TKeysHolder> getTempNode() {
			return this.tempNode;
		}

		private final void addChild(
				final Node<TFacade, TImplement, TKeysHolder> child) {
			if (this.firstChild == null) {
				this.firstChild = child;
				child.lastSibling = null;
				child.nextSibling = null;
			} else {
				if (this.firstChild.nextSibling == null) {
					this.firstChild.nextSibling = child;
					child.nextSibling = null;
					child.lastSibling = this.firstChild;
				} else {
					child.nextSibling = this.firstChild.nextSibling;
					this.firstChild.nextSibling = child;
					child.nextSibling.lastSibling = child;
					child.lastSibling = this.firstChild;
				}
			}
			child.parent = this;
		}

		private final void exciseChild(
				final Node<TFacade, TImplement, TKeysHolder> child) {
			final Node<TFacade, TImplement, TKeysHolder> firstChild = this.firstChild;
			if (firstChild == child) {
				this.firstChild = child.nextSibling;
				if (this.firstChild != null) {
					this.firstChild.lastSibling = null;
				}
			} else {
				child.lastSibling.nextSibling = child.nextSibling;
				if (child.nextSibling != null) {
					child.nextSibling.lastSibling = child.lastSibling;
				}
			}
			child.nextSibling = null;
			child.lastSibling = null;
			child.parent = null;
		}

		private final void resolve() {
			synchronized (this.holder) {
				this.state = STATE_RESOLVED;
				this.tempNode = null;
			}
		}

		private final void dispose() {
			this.state = STATE_DISPOSED;
			if (this.parent == null) {
				this.ownTree.exciseRoot(this);
			} else {
				this.parent.exciseChild(this);
			}
			if (!this.holder.isDisposed() && this.holder.forceGetOwnTreeNode() == this) {
				this.holder.tryPutToTreeNode(null);
			}
			this.tempNode = null;
		}

		@Deprecated
		final ResourceTokenLinkImplement<TFacade> getChildren() {
			ResourceTokenLinkImplement<TFacade> link = null;
			ResourceTokenLinkImplement<TFacade> last = null;
			Node<TFacade, TImplement, TKeysHolder> node = this.firstChild;
			while (node != null) {
				try {
					switch (node.state) {
					case STATE_CREATED:
						if (!this.ownTree.bindGroup.isModifiableOnCurrentThread()) {
							continue;
						}
					case STATE_RESOLVED:
						break;
					case STATE_REMOVED:
						if (this.ownTree.bindGroup.isModifiableOnCurrentThread()) {
							continue;
						}
						break;
					case STATE_DISPOSED:
						continue;
					default:
						throw new UnsupportedOperationException();
					}
					if (last == null) {
						link = last = new ResourceTokenLinkImplement<TFacade>(node.holder);
					} else {
						last.next = new ResourceTokenLinkImplement<TFacade>((node.holder));
						last = last.next;
					}
				} finally {
					node = node.nextSibling;
				}
			}
			return link;
		}

		final CacheTree<TFacade, TImplement, TKeysHolder> ownTree;

		final CacheHolder<TFacade, TImplement, TKeysHolder> holder;

		volatile Node<TFacade, TImplement, TKeysHolder> parent;

		private volatile byte state;

		private volatile Node<TFacade, TImplement, TKeysHolder> firstChild;

		private volatile Node<TFacade, TImplement, TKeysHolder> lastSibling;

		private volatile Node<TFacade, TImplement, TKeysHolder> nextSibling;

		private TempNode tempNode;

		private final class TempNode extends
				Node<TFacade, TImplement, TKeysHolder> {

			TempNode(final Node<TFacade, TImplement, TKeysHolder> newParent) {
				super(newParent, Node.this);
				Node.this.tempNode = this;
			}

			@Override
			final TempNode asTempNode() {
				return this;
			}

			Node<TFacade, TImplement, TKeysHolder> original() {
				return Node.this;
			}

			final Node<TFacade, TImplement, TKeysHolder> toNode() {
				final Node<TFacade, TImplement, TKeysHolder> ordinal = this.original();
				final Node<TFacade, TImplement, TKeysHolder> cloned = new Node<TFacade, TImplement, TKeysHolder>(null, Node.this);
				cloned.firstChild = ordinal.firstChild;
				return cloned;
			}

		}

	}

	// ------------------------------------------------------------------------
	// 兼容旧版本

	@Deprecated
	final CacheHolder<TFacade, TImplement, TKeysHolder> tryGetParentOf(
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder) {
		this.readLock.lock();
		try {
			final CacheTree.Node<TFacade, TImplement, TKeysHolder> parent = holder.findTreeNodeIn(holder.isModifiableOnCurrentThread(), false).getParent(this.bindGroup.isModifiableOnCurrentThread());
			if (parent == null) {
				return null;
			} else {
				return parent.holder;
			}
		} finally {
			this.readLock.unlock();
		}
	}

	@Deprecated
	final ResourceTokenLink<TFacade> tryGetChildrenOf(
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder) {
		this.readLock.lock();
		try {
			CacheTree.Node<TFacade, TImplement, TKeysHolder> node = holder.findTreeNodeIn(holder.isModifiableOnCurrentThread(), false);
			if (node == null) {
				return null;
			} else {
				return node.getChildren();
			}
		} finally {
			this.readLock.unlock();
		}
	}

}
